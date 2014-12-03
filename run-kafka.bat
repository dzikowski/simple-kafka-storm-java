
cd C:\kafka_2.11-0.8.2-beta
start bin\windows\zookeeper-server-start.bat config\zookeeper.properties
start bin\windows\kafka-server-start.bat config\server1.properties

TIMEOUT /T 5

start bin\windows\kafka-server-start.bat config\server2.properties
start bin\windows\kafka-server-start.bat config\server3.properties

call start bin\windows\kafka-topics.bat --create --zookeeper localhost:2181    ^
		--replication-factor 3 --partitions 3 --topic coolkafka-in
call start bin\windows\kafka-topics.bat --create --zookeeper localhost:2181    ^
		--replication-factor 3 --partitions 3 --topic coolkafka-out

TIMEOUT /T 3

start bin\windows\kafka-console-producer.bat --topic coolkafka-in              ^
		--broker-list localhost:9092,localhost:9093,localhost:9094
start bin\windows\kafka-console-consumer.bat --topic coolkafka-in              ^
		--zookeeper localhost:2181
start bin\windows\kafka-console-consumer.bat --topic coolkafka-out             ^
		--zookeeper localhost:2181
