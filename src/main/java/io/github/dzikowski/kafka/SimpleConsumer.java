package io.github.dzikowski.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class SimpleConsumer implements Runnable {
	private KafkaStream<byte[], byte[]> stream;
	private int threadNumber;

	public SimpleConsumer(KafkaStream<byte[], byte[]> stream, int threadNumber) {
		this.stream = stream;
		this.threadNumber = threadNumber;
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			String msg = new String(it.next().message());
			if ("SHUTDOWN".equals(msg))
				break;
			System.out.println("Thread " + threadNumber + ": " + msg);
		}
		System.out.println("Shutting down Thread: " + threadNumber);
	}
}