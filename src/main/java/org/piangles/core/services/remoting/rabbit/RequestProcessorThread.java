package org.piangles.core.services.remoting.rabbit;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.Service;
import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.core.services.remoting.SessionValidator;
import org.piangles.core.services.remoting.Traceable;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;

/**
 * On the server side each request needs to be processed on a separate thread.
 * This needs to be pooled eventually for faster creation of thread. 
 *
 */
public class RequestProcessorThread extends Thread implements Traceable, SessionAwareable
{
	private String serviceName = null;
	private Service service = null;
	private SessionValidator sessionValidator = null;
	private SessionDetails sessionDetails = null;
	private Request request = null;

	private Delivery delivery = null;
	private Channel channel = null;
	
	private RMQHelper rmqHelper = null;
	
	public RequestProcessorThread(String serviceName, Service service, SessionValidator sessionValidator, RMQHelper rmqHelper, Delivery delivery, Channel channel)
	{
		this.serviceName = serviceName;
		this.service = service;
		this.sessionValidator = sessionValidator;
		this.rmqHelper = rmqHelper;
		this.delivery = delivery;
		this.channel = channel;
	}
	
	public void run()
	{
		byte[] body = null;
		Response response = null;
		try
		{
			body = delivery.getBody();
			request = rmqHelper.getDecoder().decode(body, Request.class);
			sessionDetails = new SessionDetails(request.getUserId(), request.getSessionId()); 
			response = processRequest(request);
		}
		catch (Exception e)
		{
			if (request != null)
			{
				response = new Response(request.getServiceName(), request.getEndPoint(), e);
			}
			else
			{
				response = new Response(serviceName, new String(body), e);
			}
		}
		
		if (	response != null && 
				delivery.getProperties() != null && 
				delivery.getProperties().getCorrelationId() != null) //It is not fire and forget so send response
		{
			byte[] encodedBytes = null;

			try
			{
				encodedBytes = rmqHelper.getEncoder().encode(response);

				BasicProperties replyProps = new BasicProperties.Builder().
											correlationId(delivery.getProperties().getCorrelationId()).build();
				
				/**
				 * Exchange should come from configuration.
				 */
				String exchange = "";
				channel.basicPublish(exchange, delivery.getProperties().getReplyTo(), replyProps, encodedBytes);
				
				/**
				 * Why do we not need the below ack Code?
				 * 
				 * So the way the code flows currently is from the mainloop below in RpcServer
				 * 	> public ShutdownSignalException mainloop() throws IOException
				 * the call goes to 
				 * 	> public void processRequest(Delivery request) throws IOException
				 * 
				 *  processRequest is overriden in ReqRespController.
				 *  
				 *  when processRequest is returned RpcServer has the following code
				 *  > _channel.basicAck(request.getEnvelope().getDeliveryTag(), false);
				 *  
				 *  So we should not be acknowledging again from here. It is taken care by
				 *  the framework.
				 */
				//channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
			catch (Exception e)
			{
				System.err.println("Exception trying to send response because of: " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}

	}
	
	public String getServiceName()
	{
		return serviceName;
	}
	
	@Override
	public UUID getTraceId()
	{
		return request.getTraceId();
	}
	
	@Override
	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}

	protected final Response processRequest(Request request) throws Exception
	{
		long startTime = System.nanoTime();
		Response response = null;
		
		if (request != null)
		{
			if (sessionValidator.isSessionValid(request))
			{
				/**
				 * Make the actual call to the service
				 */
				response = service.process(request);
				
				long delayNS = System.nanoTime() - startTime;
				long delayMiS = TimeUnit.NANOSECONDS.toMicros(delayNS);
				long delayMS = TimeUnit.NANOSECONDS.toMillis(delayNS);
				String endpoint = request.getServiceName() + "::" + request.getEndPoint();
				System.out.println(String.format("ServerSide-TimeTaken by %s is %d MilliSeconds and %d MicroSeconds.", endpoint, delayMS, delayMiS));
			}
			else
			{
				RuntimeException e = new RuntimeException(request.getServiceName() + " : UnAuthorized Request. Session could not be validated.");
				response = new Response(request.getServiceName(), e.getMessage(), e);
			}
		}
		else
		{
			RuntimeException e = new RuntimeException("Request received was null");
			response = new Response("Unknown Service", "Unknown Endpoint", e); 
		}
		return response; 
	}
}
