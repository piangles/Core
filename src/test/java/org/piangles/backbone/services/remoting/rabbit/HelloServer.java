package org.piangles.backbone.services.remoting.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.StringRpcServer;

public class HelloServer
{
	public static void main(String[] args)
	{
		try
		{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("ec2-54-167-137-225.compute-1.amazonaws.com");
			factory.setPort(5672);
			factory.setUsername("msgUser");
			factory.setPassword("msgPassword");

			Connection conn = factory.newConnection();
			final Channel ch = conn.createChannel();

			//ch.queueDeclare("Hello", false, false, false, null);
			StringRpcServer server = new StringRpcServer(ch, "Hello")
			{
				public String handleStringCall(String request)
				{
					System.out.println("Got request: " + request);
					return "Hello, " + request + "!";
				}
			};
			server.mainloop();
		}
		catch (Exception ex)
		{
			System.err.println("Main thread caught exception: " + ex);
			ex.printStackTrace();
			System.exit(1);
		}
	}
}