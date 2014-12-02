package io.github.dzikowski.apps;


class KafkaProduceAndConsumeExample {

	public static void main(String... args) {

		// produce messages
		KafkaProduceExample.start(3000);

		// read messages
		KafkaConsumerGroupExample.main();

	}

}
