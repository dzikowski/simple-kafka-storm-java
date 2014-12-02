package io.github.dzikowski.apps;

import static io.github.dzikowski.trident.TridentIDs.*;
import io.github.dzikowski.trident.Functions;
import io.github.dzikowski.trident.OpaqueTridentKafkaSpoutBuilder;
import io.github.dzikowski.util.Sleep;

import java.util.Properties;

import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaState;
import storm.kafka.trident.TridentKafkaStateFactory;
import storm.kafka.trident.TridentKafkaUpdater;
import storm.kafka.trident.mapper.FieldNameBasedTupleToKafkaMapper;
import storm.kafka.trident.selector.DefaultTopicSelector;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Sum;
import storm.trident.testing.MemoryMapState;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.AuthorizationException;
import backtype.storm.generated.InvalidTopologyException;

class KafkaStormTridentExample {

	private static Config topologyConfig() {
		Properties props = new Properties();
		props.put("metadata.broker.list", Conf.brokerList);
		props.put("request.required.acks", "1");
		props.put("serializer.class", Conf.kafkaStringEncoder);

		Config conf = new Config();
		conf.put(TridentKafkaState.KAFKA_BROKER_PROPERTIES, props);
		return conf;
	}

	public static void main(String... args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {

		// starting to build topology
		TridentTopology topology = new TridentTopology();

		// Kafka as an opaque trident spout
		OpaqueTridentKafkaSpout spout = new OpaqueTridentKafkaSpoutBuilder(Conf.zookeeper, Conf.inputTopic).build();
		Stream stream = topology.newStream(kafkaSpout, spout);

		// mapping transaction messages to pairs: (person,amount)
		Stream atomicTransactions = stream.each(strF, Functions.mapToPersonAmount, personAmountF);

		// bolt to println data
		atomicTransactions.each(personAmountF, Functions.printlnFunction, emptyF);

		// aggregating transactions and mapping to Kafka messages
		Stream transactionsGroupped = atomicTransactions.groupBy(personF)
				.persistentAggregate(new MemoryMapState.Factory(), amountF, new Sum(), sumF).newValuesStream()
				.each(personSumF, Functions.mapToKafkaMessage, keyMessageF);

		// Kafka as a bolt -- producing to outputTopic
		TridentKafkaStateFactory stateFactory = new TridentKafkaStateFactory() //
				.withKafkaTopicSelector(new DefaultTopicSelector(Conf.outputTopic)) //
				.withTridentTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>(key, message));
		transactionsGroupped.partitionPersist(stateFactory, keyMessageF, new TridentKafkaUpdater(), emptyF);

		// submitting topology to local cluster
		new LocalCluster().submitTopology(kafkaAccountsTopology, topologyConfig(), topology.build());

		// waiting a while, then running Kafka producer
		Sleep.seconds(5);
		KafkaProduceExample.start(20);

	}
}
