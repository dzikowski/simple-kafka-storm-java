package io.github.dzikowski.trident;

import io.github.dzikowski.bank.Bank;
import io.github.dzikowski.bank.Transaction;

import java.util.HashMap;

import storm.trident.operation.ReducerAggregator;
import storm.trident.tuple.TridentTuple;

import com.google.gson.Gson;

@Deprecated
@SuppressWarnings("serial")
public class TridentBankAggregator implements ReducerAggregator<Bank> {

	@Override
	public Bank init() {
		return new Bank(new HashMap<String, Integer>());
	}

	@Override
	public Bank reduce(Bank curr, TridentTuple tuple) {
		Transaction transaction = new Gson().fromJson(tuple.getString(0), Transaction.class);
		curr.make(transaction);
		System.out.println("M:>> " + curr);
		return curr;
	}

}
