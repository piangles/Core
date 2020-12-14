package org.piangles.core.services.remoting.rabbit;

import org.piangles.core.services.remoting.AbstractRemoter;
import org.piangles.core.services.remoting.BeneficiaryThread;
import org.piangles.core.stream.EndOfStream;
import org.piangles.core.stream.StreamProcessor;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;
import org.piangles.core.stream.StreamMetadata;
import org.piangles.core.util.reflect.TypeResolver;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.GetResponse;

public final class StreamImpl<T> extends AbstractRemoter implements Stream<T>
{
	private static final String EOS = EndOfStream.class.getSimpleName();
	private Channel channel;
	private StreamDetails streamDetails;
	private boolean firstStreamlet = true;
	private StreamMetadata metadata;

	public StreamImpl(Channel channel, StreamDetails streamDetails) throws Exception
	{
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
	public void add(Object streamlet)
	{
		if (firstStreamlet)
		{
			firstStreamlet = false;
			if (metadata == null)
			{
				metadata = new StreamMetadata();
			}
			pub(metadata);
		}
		pub(streamlet);
	}

	@Override
	public void done()
	{
		pub(EOS);
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
				metadata = (StreamMetadata)getDecoder().decode(response.getBody(), StreamMetadata.class);
			}		
		}

		return metadata;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(StreamProcessor<T> processor) throws Exception
	{
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String streamletAsStr = new String(delivery.getBody());
			if (EOS.equals(streamletAsStr))
			{
				channel.basicCancel(consumerTag);	
			}
			else
			{
				Object streamlet = null;
				try
				{
					Class<?>[] typeArgs = TypeResolver.resolveRawArguments(StreamProcessor.class, processor.getClass());
					
					streamlet = getDecoder().decode(streamletAsStr.getBytes(), typeArgs[0]);
					processor.process((T)streamlet);
				}
				catch (Exception e)
				{
					System.err.println("Error processing the stream : " + e.getMessage());
					e.printStackTrace(System.err);
					channel.basicCancel(consumerTag);	
				}
			}
		};
		channel.basicConsume(streamDetails.getQueueName(), true, deliverCallback, consumerTag -> {});
		channel.close();
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

	private void pub(Object obj)
	{
		try
		{
			channel.basicPublish("", streamDetails.getQueueName(), null, getEncoder().encode(obj));
		}
		catch (Exception e)
		{
			System.err.println("Exception trying to send response because of: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}
