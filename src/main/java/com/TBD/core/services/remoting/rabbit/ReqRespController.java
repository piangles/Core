package com.TBD.core.services.remoting.rabbit;

import java.util.Properties;

import com.TBD.core.services.remoting.controllers.AbstractController;
import com.TBD.core.services.remoting.controllers.ControllerException;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public final class ReqRespController extends AbstractController
{
	private RMQHelper rmqHelper = null;
	private QueueingConsumer consumer = null;

	@Override
	public void init(Properties properties) throws ControllerException
	{
		super.init(properties);
		try
		{
			rmqHelper = new RMQHelper(true, properties);

			rmqHelper.getChannel().queueDeclare(rmqHelper.getRMQProperties().getTopic(), false, false, false, null);

			rmqHelper.getChannel().basicQos(1);

			consumer = new QueueingConsumer(rmqHelper.getChannel());
			rmqHelper.getChannel().basicConsume(rmqHelper.getRMQProperties().getTopic(), false, consumer);

		}
		catch (Exception e)
		{
			throw new ControllerException(e);
		}
	}

	@Override
	public void start() throws ControllerException
	{
		ControllerException expt = null;
		while (!isStopRequested())
		{
			QueueingConsumer.Delivery delivery = null;
			try
			{
				delivery = consumer.nextDelivery();
			}
			catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException e)
			{
				expt = new ControllerException(e);
				break;
			}
			
			RequestProcessorThread rpt = new RequestProcessorThread(getSessionValidator(), getService(), rmqHelper, delivery.getEnvelope(), delivery.getBody(), delivery.getProperties());
			rpt.start();
		}
		
		if (expt != null)
		{
			throw expt;
		}
	}
	
	@Override
	public void destroy()
	{
		rmqHelper.destroy();
	}
}
