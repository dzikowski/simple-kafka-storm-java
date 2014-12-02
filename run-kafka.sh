cd ~/kafka_2.11-0.8.2-beta
gnome-terminal -e "bin/zookeeper-server-start.sh config/zookeeper.properties"
gnome-terminal -e "bin/kafka-server-start.sh config/server1.properties"

sleep 5

gnome-terminal -e "bin/kafka-server-start.sh config/server2.properties"
gnome-terminal -e "bin/kafka-server-start.sh config/server3.properties"

zookeeper="--zookeeper localhost:2181"
create="--create $zookeeper --replication-factor 2 --partitions 5"

bin/kafka-topics.sh $create --topic coolkafka-in
bin/kafka-topics.sh $create --topic coolkafka-out

sleep 3

brokers="--broker-list localhost:9092,localhost:9093,localhost:9094"
gnome-terminal -e "bin/kafka-console-producer.sh  $brokers --topic coolkafka-in"

gnome-terminal -e "bin/kafka-console-consumer.sh $zookeeper --topic coolkafka-in"
gnome-terminal -e "bin/kafka-console-consumer.sh $zookeeper --topic coolkafka-out"
