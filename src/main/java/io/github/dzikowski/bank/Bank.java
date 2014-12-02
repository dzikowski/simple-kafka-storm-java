package io.github.dzikowski.bank;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class Bank implements Serializable {

	private final Map<String, Integer> accounts;

	public Bank(Map<String, Integer> accounts) {
		this.accounts = accounts;
	}

	public Bank() {
		this(new HashMap<String, Integer>());
	}

	public static enum TransactionState {
		SPECIAL_OK, REGULAR_OK, BLOCKED
	}

	public TransactionState make(Transaction transaction) {

		int amount = transaction.getAmount();
		String to = transaction.getTo();

		// someone earns
		if (transaction.isSpecial()) {
			accounts.put(to, currentAmount(transaction.getTo()) + amount);
			return TransactionState.SPECIAL_OK;
		}

		// money transfer
		else {
			String from = transaction.getFrom();
			int amountFrom = currentAmount(from);
			int amountTo = currentAmount(to);

			accounts.put(from, amountFrom - amount);
			accounts.put(to, amountTo + amount);

			return TransactionState.REGULAR_OK;
		}
	}

	public int currentAmount(String user) {
		Integer amount = accounts.get(user);
		return amount == null ? 0 : amount;
	}

	@Override
	public String toString() {
		return accounts.toString();
	}

}
