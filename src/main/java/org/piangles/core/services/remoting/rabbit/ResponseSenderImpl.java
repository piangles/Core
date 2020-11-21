package org.piangles.core.services.remoting.rabbit;

import com.rabbitmq.client.AMQP.BasicProperties;

import org.piangles.core.services.remoting.ResponseSender;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;

class ResponseSenderImpl implements ResponseSender 
{
	private Delivery delivery = null;
	private Channel channel = null;

	ResponseSenderImpl(Delivery delivery, Channel channel)
	{
		this.delivery = delivery;
		this.channel = channel;
	}

	public void send(byte[] encodedBytes) throws Exception
	{
		 //It is not fire and forget so send response
		if (delivery.getProperties() != null && delivery.getProperties().getCorrelationId() != null)
		{

			BasicProperties replyProps = new BasicProperties.Builder().correlationId(delivery.getProperties().getCorrelationId()).build();

			/**
			 * Exchange should come from configuration.
			 */
			String exchange = "";
			channel.basicPublish(exchange, delivery.getProperties().getReplyTo(), replyProps, encodedBytes);

			/**
			 * Why do we not need the below ack Code?
			 * 
			 * So the way the code flows currently is from the mainloop below in
			 * RpcServer > public ShutdownSignalException mainloop() throws
			 * IOException the call goes to > public void
			 * processRequest(Delivery request) throws IOException
			 * 
			 * processRequest is overriden in ReqRespController.
			 * 
			 * when processRequest is returned RpcServer has the following code
			 * > _channel.basicAck(request.getEnvelope().getDeliveryTag(),
			 * false);
			 * 
			 * So we should not be acknowledging again from here. It is taken
			 * care by the framework.
			 */
			// channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}
	}
}
