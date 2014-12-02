package io.github.dzikowski.trident;

import storm.kafka.BrokerHosts;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import backtype.storm.spout.SchemeAsMultiScheme;

public class OpaqueTridentKafkaSpoutBuilder {

	private final String zookeeper;
	private final String topic;

	public OpaqueTridentKafkaSpoutBuilder(String zookeeper, String topic) {
		this.zookeeper = zookeeper;
		this.topic = topic;
	}

	public OpaqueTridentKafkaSpout build() {
		BrokerHosts zk = new ZkHosts(zookeeper);
		TridentKafkaConfig spoutConf = new TridentKafkaConfig(zk, topic);
		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		return new OpaqueTridentKafkaSpout(spoutConf);
	}

}
