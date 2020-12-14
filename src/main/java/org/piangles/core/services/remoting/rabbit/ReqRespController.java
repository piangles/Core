package org.piangles.core.services.remoting.rabbit;

import java.io.IOException;

import org.piangles.core.resources.RabbitMQSystem;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.services.remoting.RequestProcessingThread;
import org.piangles.core.services.remoting.controllers.AbstractController;
import org.piangles.core.services.remoting.controllers.ControllerException;
import org.piangles.core.util.InMemoryConfigProvider;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.RpcServer;
import com.rabbitmq.client.ShutdownSignalException;

public final class ReqRespController extends AbstractController
{
	private RabbitMQSystem rmqSystem = null;
	private Channel channel = null;
	private RpcServer server = null;

	@Override
	public void init() throws ControllerException
	{
		try
		{
			rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(new InMemoryConfigProvider(getServiceName(), getProperties()));

			channel = rmqSystem.getConnection().createChannel();
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
			channel.queueDeclare(RabbitProps.getTopic(getProperties()), durable, exclusive, autoDelete, null);
			
			server = new RpcServer(channel, RabbitProps.getTopic(getProperties()))
			{
				@Override
				public void processRequest(Delivery delivery) throws IOException
				{
					RequestProcessingThread rpt = new RequestProcessingThread(
															getServiceName(), getService(),
															getPreApprovedSessionId(), getSessionValidator(),
															getEncoder(), getDecoder(), 
															delivery.getBody(), new ResponseSenderImpl(rmqSystem, channel, delivery));
					rpt.start();
				}
			};
			ShutdownSignalException exception = server.mainloop();
			//TODO Need to handle this
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
		rmqSystem.destroy();
	}
}
