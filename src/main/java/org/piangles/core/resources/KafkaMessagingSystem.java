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
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;

public final class KafkaMessagingSystem implements Resource
{
	private Properties msgProps = null;
	
	KafkaMessagingSystem(String serviceName, Properties msgProps) throws Exception
	{
		this.msgProps = msgProps;
	}
	
	@Override
	public void close() throws Exception
	{
	}

	public KafkaProducer<String, String> createProducer()
	{
		return new KafkaProducer<>(msgProps);
	}
	
	public KafkaConsumer<String, String> createConsumer(ConsumerProperties consumerProps)
	{
		msgProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProps.getGroupId());
		
		List<TopicPartition> partitions = consumerProps.getTopics().stream().
											map(topic -> new TopicPartition(topic.topicName, topic.partitionNo)).collect(Collectors.toList());

		/**
		 * Ensure in configuration - log compacted topics are also readEarliest.
		 * Identify the readEarliest topics, we will need to seek to beginning for them.
		 * The reason is, if regular topics or log compacted topic was consumed in previous run,  
		 * Kafka will not send it back to consumer again till an update happens on that topic.
		 * 
		 * So if we seek it back to begining 
		 * 1. Regular Topics will give all the messages.
		 * 2. Log Compacted Topic will show up with the last value. Latest values when published
		 * will automatically override the last value but to start with clients will have a value. 
		 */
		List<TopicPartition> readEarliestPartitions = consumerProps.getTopics().stream().filter(topic -> topic.readEarliest).
				map(topic -> new TopicPartition(topic.topicName, topic.partitionNo)).collect(Collectors.toList());

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(msgProps);
		consumer.assign(partitions);
		if (!readEarliestPartitions.isEmpty())
		{
			consumer.seekToBeginning(readEarliestPartitions);
		}
		
		return consumer;
	}
}
