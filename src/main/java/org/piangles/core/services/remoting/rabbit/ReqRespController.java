package org.piangles.core.services.remoting.rabbit;

import java.io.IOException;
import java.util.Properties;

import org.piangles.core.services.remoting.controllers.AbstractController;
import org.piangles.core.services.remoting.controllers.ControllerException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.RpcServer;

public final class ReqRespController extends AbstractController
{
	private RMQHelper rmqHelper = null;
	private Channel channel = null;
	private RpcServer server = null;

	@Override
	public void init(Properties properties) throws ControllerException
	{
		try
		{
			rmqHelper = new RMQHelper(getServiceName(), true, properties);

			channel = rmqHelper.getConnection().createChannel();
		}
		catch (Exception e)
		{
			throw new ControllerException(e);
		}
	}

	@Override
	public void start() throws ControllerException
	{
		try
		{
			/**
			 * This needs to come from configuration this will help in LoadBalacing
			 * even if we multithread if it is at 1. RabbitMQ will not send a message
			 * here till we send an Ack back.
			 */
			channel.basicQos(1);
			boolean durable = false; 
			boolean exclusive = false; 
			boolean autoDelete = false;
			channel.queueDeclare(rmqHelper.getRMQProperties().getTopic(), durable, exclusive, autoDelete, null);
			
			server = new RpcServer(channel, rmqHelper.getRMQProperties().getTopic())
			{
				@Override
				public void processRequest(Delivery delivery) throws IOException
				{
					RequestProcessorThread rpt = new RequestProcessorThread(
															getServiceName(), 
															getPreDeterminedSessionId(), 
															getService(), 
															getSessionValidator(), 
															rmqHelper, 
															delivery, 
															channel);
					rpt.start();
				}
			};
			server.mainloop();
		}
		catch (IOException e)
		{
			throw new ControllerException(e.getMessage(), e);
		}
	}

	@Override
	protected boolean isStopRequested()
	{
		server.terminateMainloop();
		return super.isStopRequested();
	}

	@Override
	public void destroy()
	{
		rmqHelper.destroy();
	}
}
