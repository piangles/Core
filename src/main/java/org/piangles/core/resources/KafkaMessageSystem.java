package org.piangles.core.resources;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;

public class KafkaMessageSystem
{
	private Properties msgProps = null;
	
	KafkaMessageSystem(String serviceName, Properties msgProps) throws Exception
	{
		this.msgProps = msgProps;
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
		
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(msgProps);
		consumer.assign(partitions);
	
		return consumer;
	}
}
