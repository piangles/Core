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

import java.util.List;
import java.util.Map;

class StandAloneJedis implements Jedis
{
	private redis.clients.jedis.Jedis jedis = null;
	
	StandAloneJedis(redis.clients.jedis.Jedis jedis)
	{
		this.jedis = jedis;
	}
	
	@Override
	public void close()
	{
		jedis.close();
	}

	@Override
	public Long lpush(String key, String... strings)
	{
		return jedis.lpush(key, strings);
	}

	@Override
	public String hmset(String key, Map<String, String> hash)
	{
		return jedis.hmset(key, hash);
	}

	@Override
	public Long lrem(String key, long count, String value)
	{
		return jedis.lrem(key, count, value);
	}

	@Override
	public Long del(String key)
	{
		return jedis.del(key);
	}

	@Override
	public Long expire(String key, int seconds)
	{
		return jedis.expire(key, seconds);
	}

	@Override
	public Long hset(String key, String field, String value)
	{
		return jedis.hset(key, field, value);
	}

	@Override
	public Long persist(String key)
	{
		return jedis.persist(key);
	}

	@Override
	public List<String> lrange(String key, long start, long stop)
	{
		return jedis.lrange(key, start, stop);
	}

	@Override
	public Map<String, String> hgetAll(String key)
	{
		return jedis.hgetAll(key);
	}
	
	@Override
	public String get(final String key)
	{
		return jedis.get(key);
	}
	
	@Override
	public String set(final String key, final String value)
	{
		return jedis.set(key, value);
	}
}
