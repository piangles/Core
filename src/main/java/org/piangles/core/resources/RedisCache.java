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

import org.piangles.core.util.abstractions.BoundedOp;

public final class RedisCache implements Resource
{
	private static final String CLUSTERED = "Clustered";
	
	private IRedisCache iRedisCache = null;
	
	RedisCache(String serviceName, Properties cacheProps) throws Exception
	{
		String clustered = cacheProps.getProperty(CLUSTERED);
		if (Boolean.parseBoolean(clustered))
		{
			iRedisCache = new ClusteredRedisCache(serviceName, cacheProps);
		}
		else
		{
			iRedisCache = new StandAloneRedisCache(serviceName, cacheProps);
		}
	}
	
	@Override
	public void close() throws Exception
	{
		iRedisCache.close();
	}

	public Jedis getCache()
	{
		return iRedisCache.getCache();
	}
	
	public <R> R execute(BoundedOp<Jedis, R> op) throws ResourceException
	{
		Jedis jedis = null;
		
		try
		{
			jedis = iRedisCache.getCache();
			return op.perform(jedis);
		}
		catch(Exception e)
		{
			throw new ResourceException(e);
		}
		finally
		{
			if (jedis != null)
			{
				jedis.close();
			}
		}
	}
}
