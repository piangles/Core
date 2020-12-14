package org.piangles.core.services.remoting.rabbit;

import org.piangles.core.resources.RabbitMQSystem;
import org.piangles.core.services.remoting.ResponseSender;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;

class ResponseSenderImpl implements ResponseSender 
{
	private RabbitMQSystem rmqSystem = null;	
	private Channel channel = null;
	private Delivery delivery = null;

	ResponseSenderImpl(RabbitMQSystem rmqSystem, Channel channel, Delivery delivery)
	{
		this.rmqSystem = rmqSystem;
		this.channel = channel;
		this.delivery = delivery;
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

	@Override
	public Stream<?> createStream(StreamDetails streamDetails) throws Exception
	{
		return new StreamImpl<>(rmqSystem.getConnection().createChannel(), streamDetails);
	}
}
