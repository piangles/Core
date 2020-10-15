package org.piangles.backbone.services.remoting.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.RpcClientParams;

public class HelloClient
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
			Channel ch = conn.createChannel();
			//ch.queueDeclare("Hello", false, false, false, null);
			
			RpcClientParams params = new RpcClientParams().
					channel(ch).
					exchange("").
					routingKey("Hello");
					//timeout(1000);

			RpcClient service = new RpcClient(params);

			System.out.println(service.stringCall("Rabbit"));
			conn.close();
		}
		catch (Exception e)
		{
			System.err.println("Main thread caught exception: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}