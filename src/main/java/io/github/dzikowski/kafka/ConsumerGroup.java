package io.github.dzikowski.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class ConsumerGroup {

	private final ConsumerConnector consumer;
	private final String topic;
	private ExecutorService executor;

	public ConsumerGroup(String a_topic, ConsumerConfig consumerConfig) {
		this.consumer = Consumer.createJavaConsumerConnector(consumerConfig);
		this.topic = a_topic;
	}

	public void run(int numberOfThreads) {

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, numberOfThreads);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

		// launch all the threads
		executor = Executors.newFixedThreadPool(numberOfThreads);

		// create an object to consume the messages
		int threadNumber = 0;
		for (final KafkaStream<byte[], byte[]> stream : streams) {
			executor.submit(new SimpleConsumer(stream, threadNumber));
			threadNumber++;
		}
	}

	public void shutdown() {
		if (consumer != null)
			consumer.shutdown();
		if (executor != null)
			executor.shutdown();
	}

}