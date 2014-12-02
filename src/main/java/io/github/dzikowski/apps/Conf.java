package io.github.dzikowski.apps;

/**
 * Configuration appropriate to file {@link /run-kafka.bat }.
 */
interface Conf {

    /**
     * The list of running Kafka brokers
     */
    public static final String brokerList = "localhost:9092,localhost:9093,localhost:9094";

    /**
     * Zookeper address
     */
    public static final String zookeeper = "localhost:2181";

    public static final String inputTopic = "coolkafka-in";
    public static final String outputTopic = "coolkafka-out";
    public static final Object group = "coolkafka-group";

    public static final String kafkaStringEncoder = "kafka.serializer.StringEncoder";
    public static final String kafkaDefaultEncoder = "kafka.serializer.DefaultEncoder";

}
