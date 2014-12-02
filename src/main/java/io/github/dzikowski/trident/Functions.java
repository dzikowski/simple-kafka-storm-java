package io.github.dzikowski.trident;

import io.github.dzikowski.bank.Transaction;

import java.util.Arrays;
import java.util.List;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public interface Functions {

	@SuppressWarnings("serial")
	public static BaseFunction printlnFunction = new BaseFunction() {
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			System.out.println(">>> " + tuple);
		}
	};

	/**
	 * Mapping transaction message to atomic messages, for example:
	 * <ul>
	 * <li>(Alice,Bob,500) -> [(Alice,-500), (Bob,500)]</li>
	 * <li>(SPECIAL,Alice,200) -> [(Alice,200)]</li>
	 * </ul>
	 * 
	 * Accepts only valid transaction JSONs, skips invalid ones.
	 */
	@SuppressWarnings("serial")
	public static BaseFunction mapToPersonAmount = new BaseFunction() {

		private List<Object> message(String key, Integer value) {
			return Arrays.asList(new Object[] { key, value });
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			try {
				Transaction t = new Gson().fromJson(tuple.getString(0), Transaction.class);
				if (!t.isSpecial())
					collector.emit(message(t.getFrom(), -t.getAmount()));
				collector.emit(message(t.getTo(), t.getAmount()));
			} catch (JsonSyntaxException e) {
			}
		}
	};

	@SuppressWarnings("serial")
	public static BaseFunction mapToKafkaMessage = new BaseFunction() {
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			String person = tuple.getStringByField(TridentIDs.person);
			Long amount = tuple.getLongByField(TridentIDs.sum);
			collector.emit(Arrays.asList(new Object[] { "balance", person + ":\t" + amount }));
		}
	};

}
