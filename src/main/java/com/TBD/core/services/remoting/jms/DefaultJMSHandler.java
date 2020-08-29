package com.TBD.core.services.remoting.jms;

import java.lang.reflect.Method;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import com.TBD.core.services.Request;
import com.TBD.core.services.Response;
import com.TBD.core.services.remoting.handlers.AbstractHandler;

public class DefaultJMSHandler extends AbstractHandler
{
	private TopicConnection topicConn = null; 
	private TopicSession topicSession = null;
	
	private TopicPublisher requestPublisher = null;
	private TopicSubscriber responseSubscriber = null;	

	public DefaultJMSHandler(String serviceName)
	{
		super(serviceName);
	}
	
	@Override
	public void init()
	{
		try
		{
			JMSProperties jmsProperties = new JMSProperties(getProperties()); 
			TopicConnectionFactory connFactory = (TopicConnectionFactory)Class.forName(jmsProperties.getConnectionFactoryName()).newInstance();

			topicConn = connFactory.createTopicConnection();
			topicSession = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

			//Request Publisher
			Topic requestTopic = topicSession.createTopic(jmsProperties.getPublishTopic());
			requestPublisher = topicSession.createPublisher(requestTopic);
			requestPublisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
			//Response Subscriber
			Topic responseTopic = topicSession.createTopic(topicSession.createTemporaryTopic().getTopicName());
			responseSubscriber = topicSession.createSubscriber(responseTopic);
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | JMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// close the topic connection
		//topicConn.close();
	}

	@Override
	protected Object processMethodCall(Method method, Object[] args) throws Throwable
	{
		Object result = null;
		Request request = createRequest(method, args);

		try
		{
			if (method.getReturnType().equals(Void.TYPE)) //Fire and Forget
			{
				issueRequest(request);
			}
			else
			{
				Response response = issueRequestAndReceiveResponse (request);
				result = response.getReturnValue();
			}
		}
		catch(JMSException expt)
		{
			createException(method, "Invocation Exception: " + expt.getMessage(), expt);
		}
		
		return result;
	}


	private void issueRequest(Request request) throws JMSException
	{
		String requestAsStr = null; ///new String(JSON.getEncoder().encode(request));
		
		TextMessage requestMessage = topicSession.createTextMessage();
		requestMessage.setText(requestAsStr);

		requestPublisher.publish(requestMessage);
	}

	private Response issueRequestAndReceiveResponse(Request request) throws JMSException
	{
		Response response = null;
		
		String requestAsStr = null; //new String(JSON.getEncoder().encode(request));
		TextMessage requestMessage = topicSession.createTextMessage();
		requestMessage.setText(requestAsStr);
		requestMessage.setJMSReplyTo(responseSubscriber.getTopic());
		
		//Wait for response
		TextMessage responseMessage = (TextMessage)responseSubscriber.receive();
		response = null; // (Response)JSON.getDecoder().decode(responseMessage.getText().getBytes(), Response.class);

		return response;
	}

}
