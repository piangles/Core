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

public interface Jedis
{
	public void close();
	public Long lpush(final String key, final String... strings);
	public String hmset(final String key, final Map<String, String> hash);
	public Long lrem(final String key, final long count, final String value);
	public Long del(final String key);
	public Long expire(final String key, final int seconds);
	public Long hset(final String key, final String field, final String value);
	public Long persist(final String key);
	public List<String> lrange(final String key, final long start, final long stop);
	public Map<String, String> hgetAll(final String key);
}
