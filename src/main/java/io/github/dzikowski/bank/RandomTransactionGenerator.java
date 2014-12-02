package io.github.dzikowski.bank;

import java.util.Random;

import com.google.gson.Gson;

public class RandomTransactionGenerator {

	private static Random random = new Random(System.currentTimeMillis());

	private static String[] people = new String[] { "Alice", "Bob", "Charles" };

	private static int[] amounts = new int[] { 100, 200, 500, 1000 };

	public static Transaction create() {
		int person1 = random.nextInt(people.length);
		int person2 = random.nextInt(people.length);
		int amount = amounts[random.nextInt(amounts.length)];

		if (person1 == person2) {
			return Transaction.special(people[person1], amount);
		} else {
			return Transaction.regular(people[person1], people[person2], amount);
		}
	}

	public static String createJson() {
		return new Gson().toJson(create());
	}

	private RandomTransactionGenerator() {
	}
}
