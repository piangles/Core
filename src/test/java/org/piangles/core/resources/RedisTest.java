package org.piangles.core.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisTest
{
	JedisPool pool = null;
	Jedis jedis = null;

	public static void main(String[] args)
	{
		RedisTest cache = new RedisTest();
		cache.hash();
	}
	
	RedisTest()
	{
		pool = new JedisPool(new JedisPoolConfig(), "localhost", 6379, 2 * 1000, null);
	}
	
	public void list()
	{
		jedis = pool.getResource();

		String userId = "yu343hk";
		String id1 = "1";
		String id2 = "2";
		String id3 = "3";
		jedis.lpush(userId, id1, id2, id3);
		List<String> ids = null;
		ids = jedis.lrange(userId, 0, 100);
		System.out.println("IDS in cache::" + ids);
		ids.add("4");

		System.out.println("IDS in cache POST::" + jedis.llen(userId));

		//REMOVE
		jedis.lrem(userId, 1, id3);
		
		ids = jedis.lrange(userId, 0, 100);
		System.out.println("IDS in cache FINAL::" + ids);
	}
	
	public void hash()
	{
		System.out.println("getNumActive:: " + pool.getNumActive());

		jedis = pool.getResource();
		System.out.println("getNumActive:: " + pool.getNumActive());

		String sessionId = "yu343hk-asdks343-sfsd343";
		HashMap<String, String> map = new HashMap<>();
		map.put("time1", "12345");
		map.put("time2", "67890");
		jedis.hmset(sessionId, map);

		jedis.close();
		System.out.println("getNumActive:: " + pool.getNumActive());
		
		jedis = pool.getResource();
		
		Map<String, String> vals = null;
		vals = jedis.hgetAll(sessionId);
		System.out.println("VALS:::" + vals);
		
		jedis.hset(sessionId, "time2", "6789");

		vals = jedis.hgetAll(sessionId);
		System.out.println("VALS:::" + vals);
		
		//REMOVE
		jedis.del(sessionId);

		vals = jedis.hgetAll(sessionId);
		System.out.println("VALS:::" + vals);
	}
	
	public void get()
	{
		jedis = pool.getResource();

		System.out.println(jedis.get("Test"));
	}
	
	public void set()
	{
		jedis = pool.getResource();

		jedis.set("Test", "PojoSerialized as JSON");
		
		//Not supported in 3.3
		//pool.returnResource(jedis);
		pool.destroy();
	}
}
