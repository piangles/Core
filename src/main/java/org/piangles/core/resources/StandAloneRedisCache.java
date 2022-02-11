/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.core.resources;

import java.util.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

final class StandAloneRedisCache implements IRedisCache
{
	private static final String HOST = "Host";
	private static final String PORT = "Port";
	private static final String SOCKET_TIMEOUT = "SocketTimeout"; //java.net.Socket.setSoTimeout
	private static final String PASSWORD = "Password";
	private static final String MAX_TOTAL = "MaxTotal";

	private JedisPool pool = null;

	StandAloneRedisCache(String serviceName, Properties cacheProps) throws Exception
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
	
	@Override
	public void close()
	{
		pool.close();
	}

	public Jedis getCache()
	{
		return new StandAloneJedis(pool.getResource());
	}
}
