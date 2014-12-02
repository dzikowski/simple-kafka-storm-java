package io.github.dzikowski.util;

/**
 * Just sleeps (without throwing an InterruptedException)
 */
public final class Sleep {

	private Sleep() {
	}

	public static void millis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ie) {

		}
	}

	public static void seconds(long seconds) {
		millis(seconds * 1000);
	}

}
