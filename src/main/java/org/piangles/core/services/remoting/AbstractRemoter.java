package org.piangles.core.services.remoting;

import java.util.Properties;

import org.piangles.core.util.coding.Decoder;
import org.piangles.core.util.coding.Encoder;

public class AbstractRemoter
{
	private static final String ENCODER_CLASS_NAME = "EncoderClassName";
	private static final String DECODER_CLASS_NAME = "DecoderClassName";

	private String serviceName = null;
	private Properties properties = null;
	
	private Encoder encoder = null;
	private Decoder decoder = null;

	public void init(String serviceName, Properties props) throws Exception
	{
		this.serviceName = serviceName;
		this.properties = props;

		String encoderClassName = props.getProperty(ENCODER_CLASS_NAME);
		String decoderClassName = props.getProperty(DECODER_CLASS_NAME);
		
		if (encoderClassName != null)
		{
			encoder = (Encoder)Class.forName(encoderClassName).newInstance();
		}

		if (decoderClassName != null)
		{
			decoder = (Decoder)Class.forName(decoderClassName).newInstance();
		}
		
		if (encoder == null || decoder == null)
		{
			throw new Exception("Either of the " + ENCODER_CLASS_NAME + " or " + DECODER_CLASS_NAME + " properties is not defined in Discovery.");
		}
	}
	
	protected final String getServiceName()
	{
		return serviceName;
	}
	
	protected final Encoder getEncoder()
	{
		return encoder;
	}

	protected final Decoder getDecoder()
	{
		return decoder;
	}
	
	protected final Properties getProperties()
	{
		return properties;
	}
}
