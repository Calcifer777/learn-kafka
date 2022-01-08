#!/bin/bash

kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic test --partitions 2 --replication-factor 1

kafka-console-producer.sh --bootstrap-server 127.0.0.1:9092 --topic test

kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic test --from-beginning
