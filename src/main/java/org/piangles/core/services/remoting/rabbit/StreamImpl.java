package org.piangles.core.services.remoting.rabbit;

import java.io.IOException;
import java.util.Properties;

import org.piangles.core.services.remoting.AbstractRemoter;
import org.piangles.core.services.remoting.BeneficiaryThread;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;
import org.piangles.core.stream.StreamMetadata;
import org.piangles.core.stream.StreamProcessor;
import org.piangles.core.stream.Streamlet;

import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

public final class StreamImpl<T> extends AbstractRemoter implements Stream<T>
{
	private Channel channel;
	private StreamDetails streamDetails;
	private boolean firstStreamlet = true;
	private StreamMetadata metadata;

	public StreamImpl(String serviceName, Properties props, Channel channel, StreamDetails streamDetails) throws Exception
	{
		super.init(serviceName, props);
		this.channel = channel;
		this.streamDetails = streamDetails;

		channel.queueDeclare(streamDetails.getQueueName(), false, false, false, null);
	}

	@Override
	public void setMetadata(StreamMetadata metadata)
	{
		this.metadata = metadata;
	}

	@Override
	public void add(T payload)
	{
		if (firstStreamlet)
		{
			firstStreamlet = false;
			if (metadata == null)
			{
				metadata = new StreamMetadata();
			}
			pub(new Streamlet<T>(metadata));
		}
		pub(new Streamlet<T>(payload));
	}

	@Override
	public void done()
	{
		pub(new Streamlet<T>());
		try
		{
			channel.close();
		}
		catch (Exception e)
		{
			System.err.println("Exception trying to close channel because of: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	@Override
	public StreamMetadata getMetadata() throws Exception
	{
		if (firstStreamlet)
		{
			firstStreamlet = false;
			GetResponse response = channel.basicGet(streamDetails.getQueueName(), true);
			if (response != null) 
			{
				Streamlet<T> streamlet = getDecoder().decode(response.getBody(), new TypeToken<Streamlet<T>>() {}.getType());
				metadata = streamlet.getMetadata();
			}		
		}

		return metadata;
	}

	@Override
	public void process(StreamProcessor<T> processor) throws Exception
	{
		Consumer consumer = new DefaultConsumer(channel)
		{
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
			{
				Streamlet<T> streamlet = null;
				try
				{
					streamlet = getDecoder().decode(body, new TypeToken<Streamlet<T>>() {}.getType());

					/**
					 * This method helps get the type of <T> of a Geneneric interfaces instance.
					 * Class<?>[] typeArgs = TypeResolver.resolveRawArguments(StreamProcessor.class, processor.getClass()); 
					 */
				}
				catch (Exception e)
				{
					System.err.println("Error processing the stream : " + e.getMessage());
					e.printStackTrace(System.err);
					channel.basicCancel(consumerTag);
					return;
				}
				
				if (streamlet.isEndOfStreamMessage())
				{
					channel.basicCancel(consumerTag);
//					try
//					{
//						channel.close();
//					}
//					catch (TimeoutException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
				else if (StreamMetadata.class.getCanonicalName().equals(streamlet.getType()))
				{
					firstStreamlet = false;
					metadata = streamlet.getMetadata();						
				}
				else
				{
					processor.process(streamlet.getPayload());
				}
			}
		};
		channel.basicConsume(streamDetails.getQueueName(), true, consumer);
		
//		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//			String streamletAsStr = new String(delivery.getBody());
//			if (EOS.equals(streamletAsStr))
//			{
//				channel.basicCancel(consumerTag);	
//			}
//			else
//			{
//				Object streamlet = null;
//				try
//				{
//					Class<?>[] typeArgs = TypeResolver.resolveRawArguments(StreamProcessor.class, processor.getClass());
//					
//					streamlet = getDecoder().decode(streamletAsStr.getBytes(), typeArgs[0]);
//					processor.process((T)streamlet);
//				}
//				catch (Exception e)
//				{
//					System.err.println("Error processing the stream : " + e.getMessage());
//					e.printStackTrace(System.err);
//					channel.basicCancel(consumerTag);	
//				}
//			}
//		};
//		channel.basicConsume(streamDetails.getQueueName(), true, deliverCallback, consumerTag -> {});
		//channel.close();
	}

	@Override
	public void processAsync(StreamProcessor<T> processor)
	{
		BeneficiaryThread t = new BeneficiaryThread(() -> { //Should inherit calling thread's TraceId and SessionId
			try
			{
				process(processor);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		});
		t.start();
	}

	private void pub(Streamlet<?> streamlet)
	{
		try
		{
			channel.basicPublish("", streamDetails.getQueueName(), null, getEncoder().encode(streamlet));
		}
		catch (Exception e)
		{
			System.err.println("Exception trying to send response because of: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}
