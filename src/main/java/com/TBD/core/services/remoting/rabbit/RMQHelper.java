package com.TBD.core.services.remoting.rabbit;

import java.io.IOException;
import java.util.Properties;

import com.TBD.core.util.abstractions.Decrypter;
import com.TBD.core.util.coding.Decoder;
import com.TBD.core.util.coding.Encoder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public final class RMQHelper
{
	private static final String CONTROLLER_DECRYPTER_CLASS_NAME = "ControllerDecrypterClassName";
	private static final String CONTROLLER_DECRYPTER_AUTHZ_ID = "ControllerDecrypterAuthorizationId";
	
	private static final String HANDLER_DECRYPTER_CLASS_NAME = "HandlerDecrypterClassName";
	private static final String HANDLER_DECRYPTER_AUTHZ_ID = "HandlerDecrypterAuthorizationId";
	
	private static final String ENCODER_CLASS_NAME = "EncoderClassName";
	private static final String DECODER_CLASS_NAME = "DecoderClassName";
	
	private String serviceName = null;
	private boolean controllerCall = false;
	private RMQProperties rmqProperties = null;
	
	private Connection connection = null;
	private Channel channel = null;
	
	private Encoder encoder = null;
	private Decoder decoder = null;

	public RMQHelper(String serviceName, boolean controllerCall, Properties properties) throws Exception
	{
		this.serviceName = serviceName;
		this.controllerCall = controllerCall;

		rmqProperties = createRMQProperties(properties);
		createCoders(properties);
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(rmqProperties.getHost());
		factory.setPort(rmqProperties.getPort());
		factory.setUsername(rmqProperties.getLogin());
		factory.setPassword(rmqProperties.getPassword());
		
		connection = factory.newConnection();
		channel = connection.createChannel();
	}

	public Channel getChannel()
	{
		return channel;
	}
	
	public RMQProperties getRMQProperties()
	{
		return rmqProperties;
	}
	
	public Encoder getEncoder()
	{
		return encoder;
	}

	public Decoder getDecoder()
	{
		return decoder;
	}

	public void destroy()
	{
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private  RMQProperties createRMQProperties(Properties props) throws Exception
	{
		try
		{
			Decrypter decrypter = null;
			String decrypterClassName = null;
			String decrypterAuthorizationIdName;
			String decrypterAuthorizationId = null;
			if (controllerCall)
			{
				decrypterClassName = props.getProperty(CONTROLLER_DECRYPTER_CLASS_NAME);
				decrypterAuthorizationIdName = CONTROLLER_DECRYPTER_AUTHZ_ID;
				decrypterAuthorizationId = props.getProperty(CONTROLLER_DECRYPTER_AUTHZ_ID);
			}
			else
			{
				decrypterClassName = props.getProperty(HANDLER_DECRYPTER_CLASS_NAME);
				decrypterAuthorizationIdName = HANDLER_DECRYPTER_AUTHZ_ID;
				decrypterAuthorizationId = props.getProperty(HANDLER_DECRYPTER_AUTHZ_ID);
			}
			
			if (decrypterClassName != null)
			{
				decrypter = (Decrypter)Class.forName(decrypterClassName).getConstructor(String.class).newInstance(serviceName);
			}
			
			if (decrypter != null)
			{
				decrypter.init(decrypterAuthorizationIdName, decrypterAuthorizationId);
				
				System.out.println("RMQProperties.LOGIN : " + RMQProperties.LOGIN);
				props.setProperty(RMQProperties.LOGIN, decrypter.decrypt(RMQProperties.LOGIN, props.getProperty(RMQProperties.LOGIN)));
				props.setProperty(RMQProperties.PASSWORD, decrypter.decrypt(RMQProperties.PASSWORD, props.getProperty(RMQProperties.PASSWORD)));
			}
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		
		return new RMQProperties(props);
	}
	
	private void createCoders(Properties props) throws Exception
	{
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
}
