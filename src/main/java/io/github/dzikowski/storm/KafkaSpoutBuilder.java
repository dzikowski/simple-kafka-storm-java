package io.github.dzikowski.storm;

import java.util.UUID;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.spout.SchemeAsMultiScheme;

public class KafkaSpoutBuilder {

	private final String zooKeeper;
	private final String topic;

	public KafkaSpoutBuilder(String zooKeeper, String topic) {
		this.zooKeeper = zooKeeper;
		this.topic = topic;
	}

	public KafkaSpout build() {
		BrokerHosts hosts = new ZkHosts(zooKeeper);
		SpoutConfig spoutConfig = new SpoutConfig(hosts, topic, "/" + topic, UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		return new KafkaSpout(spoutConfig);
	}

}
