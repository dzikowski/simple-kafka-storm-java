package io.github.dzikowski.kafka;

import java.util.Random;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

public class VerbosePartitioner implements Partitioner {

	private static Random random = new Random();

	public VerbosePartitioner(VerifiableProperties props) {
	}

	public int partition(Object key, int a_numPartitions) {
		int chosenPartition = random.nextInt(a_numPartitions);
		System.out.println(key.toString() + " -> p" + chosenPartition);
		return chosenPartition;
	}

}