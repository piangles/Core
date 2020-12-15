package org.piangles.core.services.remoting.rabbit;

import java.io.IOException;
import java.util.Properties;

import org.piangles.core.services.remoting.AbstractRemoter;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;
import org.piangles.core.stream.StreamMetadata;
import org.piangles.core.stream.StreamProcessingThread;
import org.piangles.core.stream.StreamProcessor;
import org.piangles.core.stream.Streamlet;

import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

public final class StreamImpl<I> extends AbstractRemoter implements Stream<I>
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
	public void add(I payload)
	{
		if (firstStreamlet)
		{
			firstStreamlet = false;
			if (metadata == null)
			{
				metadata = new StreamMetadata();
			}
			pub(new Streamlet<I>(metadata));
		}
		pub(new Streamlet<I>(payload));
	}

	@Override
	public void done()
	{
		pub(new Streamlet<I>());
		close();
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
				Streamlet<I> streamlet = getDecoder().decode(response.getBody(), new TypeToken<Streamlet<I>>() {}.getType());
				metadata = streamlet.getMetadata();
			}		
		}

		return metadata;
	}

	@Override
	public <O> void processAsync(StreamProcessor<I,O> processor) throws Exception
	{
		processAsync(new StreamProcessingThread<>(processor));
	}
	
	public <O> void processAsync(StreamProcessingThread<I,O> spt) throws Exception
	{
		spt.start();
		
		Consumer consumer = new DefaultConsumer(channel)
		{
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
			{
				Streamlet<I> streamlet = null;
				try
				{
					streamlet = getDecoder().decode(body, new TypeToken<Streamlet<I>>() {}.getType());

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
				
				/**
				 * Processing of streamlet should be called from a 
				 * BeneficiaryThread instance. That way it inherits 
				 * the session and traceId
				 * 
				 * so put the streamlet(except metadata) in a queue that 
				 * the BeneficiaryThread can read from and process. 
				 */
				if (StreamMetadata.class.getCanonicalName().equals(streamlet.getType()))
				{
					firstStreamlet = false;
					metadata = streamlet.getMetadata();						
				}
				else if (!streamlet.isEndOfStreamMessage())
				{
					spt.getBlockingQueue().offer(streamlet);
				}
				else//It is EndOfStreamMessage
				{
					channel.basicCancel(consumerTag);
					channel.queueDelete(streamDetails.getQueueName());
					close();
					
					spt.getBlockingQueue().offer(streamlet);
				}
			}
		};
		channel.basicConsume(streamDetails.getQueueName(), true, consumer);
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
	
	private void close()
	{
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
}
