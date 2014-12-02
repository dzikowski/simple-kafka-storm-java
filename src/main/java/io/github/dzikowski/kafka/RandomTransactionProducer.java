package io.github.dzikowski.kafka;

import io.github.dzikowski.bank.RandomTransactionGenerator;
import io.github.dzikowski.util.Sleep;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Using Kafka, sends @eventsToSend transactions. Every 100 events sleeps 300ms.
 * 
 */
public class RandomTransactionProducer implements Runnable {

    private final int eventsToSend;
    private final String topic;
    private final ProducerConfig producerConfig;

    public RandomTransactionProducer(int eventsToSend, String topic, ProducerConfig producerConfig) {
        this.eventsToSend = eventsToSend;
        this.topic = topic;
        this.producerConfig = producerConfig;
    }

    @Override
    public void run() {

        Producer<String, String> producer = new Producer<String, String>(producerConfig);

        for (long nEvents = 0; nEvents < eventsToSend; nEvents++) {

            // sleep
            if (nEvents % 100 == 0)
                Sleep.millis(300);

            // random transaction JSON to send as message
            String msg = RandomTransactionGenerator.createJson();

            // BTW: when key is not given, Kafka does not use our partitioner
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, "transaction", msg);
            producer.send(data);
        }

        producer.close();
    }

}