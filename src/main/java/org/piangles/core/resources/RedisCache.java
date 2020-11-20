package org.piangles.core.resources;

import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisCache
{
	private static final String HOST = "Host";
	private static final String PORT = "Port";
	private static final String SOCKET_TIMEOUT = "SocketTimeout"; //java.net.Socket.setSoTimeout
	private static final String PASSWORD = "Password";
	private static final String MAX_TOTAL = "MaxTotal";

	private JedisPool pool = null;

	RedisCache(String serviceName, Properties cacheProps) throws Exception
	{
		String host, password;
		int port, socketTimeout, maxTotal;
		try
		{
			host = cacheProps.getProperty(HOST);
			port = Integer.parseInt(cacheProps.getProperty(PORT));
			socketTimeout = Integer.parseInt(cacheProps.getProperty(SOCKET_TIMEOUT));
			socketTimeout = socketTimeout * 1000; //Convert from Seconds to Milliseconds
			password = cacheProps.getProperty(PASSWORD);
			maxTotal = Integer.parseInt(cacheProps.getProperty(MAX_TOTAL));
		}
		catch (Exception e)
		{
			throw new Exception(String.format("Could not parse one of the properties [%s,%s,%s]", PORT, SOCKET_TIMEOUT, MAX_TOTAL), e);
		}
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(maxTotal);

		pool = new JedisPool(poolConfig, host, port, socketTimeout, password);
	}
	
	public Jedis getCache()
	{
		return pool.getResource();
	}
}
