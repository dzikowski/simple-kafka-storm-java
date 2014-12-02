package io.github.dzikowski.trident;

import backtype.storm.tuple.Fields;

public interface TridentIDs {
	public static final String kafkaSpout = "kafkaSpout";
	public static final String kafkaAccountsTopology = "kafkaTridentAccountsTopology";

	public static final String amount = "amount";
	public static final String key = "key";
	public static final String message = "message";
	public static final String person = "person";
	public static final String str = "str";
	public static final String sum = "sum";

	public static final Fields emptyF = new Fields();

	public static final Fields amountF = new Fields(amount);
	public static final Fields personF = new Fields(person);
	public static final Fields strF = new Fields(str);
	public static final Fields sumF = new Fields(sum);

	public static final Fields personAmountF = new Fields(person, amount);
	public static final Fields keyMessageF = new Fields(key, message);
	public static final Fields personSumF = new Fields(person, sum);
}