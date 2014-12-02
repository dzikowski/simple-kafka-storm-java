package io.github.dzikowski.apps;

import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import io.github.dzikowski.kafka.ConsumerGroup;
import io.github.dzikowski.util.Sleep;

class KafkaConsumerGroupExample {

	private static ConsumerConfig consumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", Conf.zookeeper);
		props.put("group.id", Conf.group);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");

		return new ConsumerConfig(props);
	}

	public static void main(String... args) {

		ConsumerGroup group = new ConsumerGroup(Conf.inputTopic, consumerConfig());
		group.run(3);

		Sleep.seconds(20);

		group.shutdown();
	}

}
