package io.github.dzikowski.storm;

import io.github.dzikowski.bank.Bank;
import io.github.dzikowski.bank.Transaction;
import io.github.dzikowski.bank.Bank.TransactionState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@SuppressWarnings("serial")
public class BankAccountBolt extends BaseRichBolt {

	private OutputCollector collector;

	private final Bank bank = new Bank(new HashMap<String, Integer>());

	@Override
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		String transactionJson = input.getString(0);
		try {
			Transaction transaction = new Gson().fromJson(transactionJson, Transaction.class);
			TransactionState state = bank.make(transaction);

			if (TransactionState.SPECIAL_OK.equals(state)) {
				collector.emit(specialMessage(transaction));
			} else if (TransactionState.REGULAR_OK.equals(state)) {
				collector.emit(okMessage(transaction));
			} else {
				collector.emit(invalidTransactionMessage(transactionJson));
			}
		} catch (JsonSyntaxException e) {
			collector.emit(invalidTransactionMessage(transactionJson));
		}
		collector.ack(input);
	}

	private List<Object> okMessage(Transaction t) {
		int amountFrom = bank.currentAmount(t.getFrom());
		int amountTo = bank.currentAmount(t.getTo());
		String msgTmpl = "OK: %s (%d) pays %d to %s (%d)";
		String msg = String.format(msgTmpl, t.getFrom(), amountFrom, t.getAmount(), t.getTo(), amountTo);
		return Arrays.asList(new Object[] { msg });
	}

	private List<Object> specialMessage(Transaction t) {
		int amount = bank.currentAmount(t.getTo());
		String msg = String.format("OK: %s (%d) earns %d", t.getTo(), amount, t.getAmount());
		return Arrays.asList(new Object[] { msg });
	}

	private List<Object> invalidTransactionMessage(String json) {
		return Arrays.asList(new Object[] { "ERROR: cannot process transaction: " + json });
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("message"));
	}
}