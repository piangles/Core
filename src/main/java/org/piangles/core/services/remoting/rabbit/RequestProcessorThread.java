package org.piangles.core.services.remoting.rabbit;

import java.util.UUID;

import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.Service;
import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.core.services.remoting.SessionValidator;
import org.piangles.core.services.remoting.Traceable;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

/**
 * On the server side each request needs to be processed on a separate thread.
 * This needs to be pooled eventually for faster creation of thread. 
 *
 */
public class RequestProcessorThread extends Thread implements Traceable, SessionAwareable
{
	private RMQHelper rmqHelper = null;
	private Service service = null;
	private Envelope envelope = null;
	private byte[] body = null;
	private Request request = null;
	private BasicProperties props = null;
	private BasicProperties replyProps = null;
	private SessionDetails sessionDetails = null;
	private SessionValidator sessionValidator = null;
	
	public RequestProcessorThread(SessionValidator sessionValidator, Service service, RMQHelper rmqHelper, Envelope envelope, byte[] body, BasicProperties props)
	{
		this.sessionValidator = sessionValidator;
		this.service = service;
		this.rmqHelper = rmqHelper;
		this.envelope = envelope;
		this.body = body;
		this.props = props;
		if (props != null)
		{
			replyProps = new BasicProperties.Builder().correlationId(props.getCorrelationId()).build();
		}
	}
	
	public void run()
	{
		Response response = null;
		try
		{
			request = rmqHelper.getDecoder().decode(body, Request.class);
			sessionDetails = new SessionDetails(request.getUserId(), request.getSessionId()); 
			response = processRequest(request);
		}
		catch (Exception e)
		{
			response = new Response(request.getServiceName(), request.getEndPoint(), e);
		}
		
		if (response != null && replyProps != null) //It is not fire and forget so send response
		{
			byte[] encodedBytes = null;
			try
			{
				encodedBytes = rmqHelper.getEncoder().encode(response);
				
				rmqHelper.getChannel().basicPublish("", props.getReplyTo(), replyProps, encodedBytes);
				rmqHelper.getChannel().basicAck(envelope.getDeliveryTag(), false);
			}
			catch (Exception e)
			{
				//TODO Notify
				e.printStackTrace();
			}
		}

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
		long startTime = System.currentTimeMillis();
		Response response = null;
		
		if (request != null)
		{
			if (sessionValidator.isSessionValid(request))
			{
				response = service.process(request);
				System.out.println(String.format("ServerSide-TimeTaken by %s is %d MilliSeconds.", request.getEndPoint(), (System.currentTimeMillis() - startTime)));
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
