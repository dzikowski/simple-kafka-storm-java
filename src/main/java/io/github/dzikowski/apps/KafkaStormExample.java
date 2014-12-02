package io.github.dzikowski.apps;

import io.github.dzikowski.storm.BankAccountBolt;
import io.github.dzikowski.storm.KafkaSpoutBuilder;
import io.github.dzikowski.storm.PrintlnBolt;
import io.github.dzikowski.storm.TransactionTupleToKafkaMapper;
import io.github.dzikowski.util.Sleep;

import java.util.Properties;

import storm.kafka.bolt.KafkaBolt;
import storm.kafka.bolt.selector.DefaultTopicSelector;
import storm.kafka.trident.TridentKafkaState;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.AuthorizationException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

class KafkaStormExample {

	private static interface IDs {
		public static final String kafkaSpout = "kafkaSpout";
		public static final String printlnBolt = "printlnBolt";
		public static final String kafkaBolt = "kafkaBolt";
		public static final String userAccountBolt = "userAccountBolt";
		public static final String kafkaAccountsTopology = "kafkaAccountsTopology";
	}

	private static Config topologyConfig() {
		Properties props = new Properties();
		props.put("metadata.broker.list", Conf.brokerList);
		props.put("request.required.acks", "1");
		props.put("serializer.class", Conf.kafkaStringEncoder);

		Config conf = new Config();
		conf.put(TridentKafkaState.KAFKA_BROKER_PROPERTIES, props);
		return conf;
	}

	/**
	 * 
	 * This example is very dangerous to the consistency of your bank accounts. Guess why, or read the
	 * tutorial.
	 * 
	 * @throws AlreadyAliveException
	 * @throws InvalidTopologyException
	 * @throws AuthorizationException
	 */
	public static void main(String... args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {

		// starting to build topology
		TopologyBuilder builder = new TopologyBuilder();

		// Kafka as a spout
		builder.setSpout(IDs.kafkaSpout, new KafkaSpoutBuilder(Conf.zookeeper, Conf.inputTopic).build());

		// bolt to println data
		builder.setBolt(IDs.printlnBolt, new PrintlnBolt()).shuffleGrouping(IDs.kafkaSpout);

		// bolt to perform transactions and simulate bank accounts
		builder.setBolt(IDs.userAccountBolt, new BankAccountBolt()).shuffleGrouping(IDs.kafkaSpout);

		// Kafka as a bolt -- sending messages to the output topic
		KafkaBolt<Object, Object> bolt = new KafkaBolt<>().withTopicSelector(new DefaultTopicSelector(Conf.outputTopic))
				.withTupleToKafkaMapper(new TransactionTupleToKafkaMapper());
		builder.setBolt(IDs.kafkaBolt, bolt).shuffleGrouping(IDs.userAccountBolt);

		// submit topolody to local cluster
		new LocalCluster().submitTopology(IDs.kafkaAccountsTopology, topologyConfig(), builder.createTopology());

		// wait a while, then simulate random transaction stream to Kafka
		Sleep.seconds(5);
		KafkaProduceExample.start(2000);

	}
}
