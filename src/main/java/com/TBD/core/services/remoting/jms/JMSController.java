package com.TBD.core.services.remoting.jms;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import com.TBD.core.services.Service;
import com.TBD.core.services.remoting.controllers.Controller;
import com.TBD.core.services.remoting.controllers.ControllerException;

public class JMSController implements Controller
{
	private Service service;

	private TopicSession topicSession = null;
	private TopicPublisher topicPublisher = null;

	@Override
	public void init(String serviceName, Properties properties) throws ControllerException
	{
		
	}
	
	@Override
	public void start(Service service) throws ControllerException
	{
//		// lookup the topic connection factory
//		TopicConnectionFactory connFactory = null; //Class.forName("");
//
//		// create a topic connection
//		TopicConnection topicConn = connFactory.createTopicConnection();
//
//		// create a topic session
//		topicSession = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
//
//		// create a topic publisher
//		Topic topic = topicSession.createTopic(topicName);
//		topicPublisher = topicSession.createPublisher(topic);
//		topicPublisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//
//		
//		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		Destination requestQueue = JndiUtil.getDestination(requestQueueName);
//		Destination invalidQueue = JndiUtil.getDestination(invalidQueueName);
//
//		MessageConsumer requestConsumer = session.createConsumer(requestQueue);
//		MessageListener listener = this;
//		requestConsumer.setMessageListener(listener);
//		
//		// close the topic connection
//		//topicConn.close();

	}

	@Override
	public void destroy() throws ControllerException
	{
		
	}
	
	class RequestListener implements MessageListener
	{
		public void onMessage(Message message)
		{
			try
			{
				if ((message instanceof TextMessage) && (message.getJMSReplyTo() != null)) 
				{
					TextMessage requestMessage = (TextMessage) message;
					
//					System.out.println("Received request");
//					System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
//					System.out.println("\tMessage ID: " + requestMessage.getJMSMessageID());
//					System.out.println("\tCorrel. ID: " + requestMessage.getJMSCorrelationID());
//					System.out.println("\tReply to:   " + requestMessage.getJMSReplyTo());
//					System.out.println("\tContents:   " + requestMessage.getText());
//
//					Request request = (Request)JSON.getDecoder().decode(requestMessage.getText().getBytes(), Request.class);
//					Response response = service.process(request);
//					
//					if (response != null)
//					{
//						Destination replyDestination = message.getJMSReplyTo();
//						MessageProducer replyProducer = session.createProducer(replyDestination);
//						TextMessage replyMessage = session.createTextMessage();
//						replyMessage.setText(contents);
//						replyMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());
//						replyProducer.send(replyMessage);
//					}
				}
			}
			catch (JMSException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() throws ControllerException
	{
		// TODO Auto-generated method stub
		
	}
}
