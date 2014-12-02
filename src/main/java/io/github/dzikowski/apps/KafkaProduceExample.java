package io.github.dzikowski.apps;

import io.github.dzikowski.kafka.RandomTransactionProducer;
import io.github.dzikowski.kafka.VerbosePartitioner;

import java.util.Properties;

import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

/**
 * Produces random transaction JSONs to topic {@link Conf.inputTopic }. Uses {@link VerbosePartitioner} to
 * display partition results.
 */
class KafkaProduceExample {

	private static ProducerConfig producerConfig() {
		Properties props = new Properties();
		props.put("metadata.broker.list", Conf.brokerList);
		props.put("serializer.class", StringEncoder.class.getName());
		props.put("partitioner.class", VerbosePartitioner.class.getName());
		props.put("request.required.acks", "1");
		return new ProducerConfig(props);
	}

	public static void start(int numberOfTransactions) {
		Runnable producer = new RandomTransactionProducer(numberOfTransactions, Conf.inputTopic, producerConfig());
		new Thread(producer).start();
	}

	public static void main(String... args) {
		start(3000);
	}
}