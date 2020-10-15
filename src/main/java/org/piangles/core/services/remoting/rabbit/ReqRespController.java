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
			channel.basicQos(1);
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
			server = new RpcServer(channel, rmqHelper.getRMQProperties().getTopic())
			{
				@Override
				public void processRequest(Delivery delivery) throws IOException
				{
					RequestProcessorThread rpt = new RequestProcessorThread(getServiceName(), getService(), getSessionValidator(), rmqHelper, delivery, channel);
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
