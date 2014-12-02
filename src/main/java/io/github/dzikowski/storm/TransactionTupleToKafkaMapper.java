package io.github.dzikowski.storm;

import backtype.storm.tuple.Tuple;
import storm.kafka.bolt.mapper.TupleToKafkaMapper;

@SuppressWarnings("serial")
public class TransactionTupleToKafkaMapper implements TupleToKafkaMapper<Object,Object> {

	@Override
	public String getKeyFromTuple(Tuple tuple) {
		return null;
	}

	@Override
	public String getMessageFromTuple(Tuple tuple) {
		return tuple.getString(0);
	}

}
