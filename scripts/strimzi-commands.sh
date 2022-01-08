#!/bin/bash

kubectl -n kafka run kafka-producer \
  -ti \
  --image=quay.io/strimzi/kafka:0.27.0-kafka-3.0.0 \
  --rm=true \
  --restart=Never \
  -- /bin/sh 

# Commands are in the bin folder (under home)
export PATH=$PATH:bin/

# kafka-topcs

bin/kafka-topics.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --list

bin/kafka-topics.sh  \
  --bootstrap-server my-cluster-kafka-bootstrap:9092  \
  --topic first_topic  \
  --create  \
  --partitions 3  \
  --replication-factor 1

bin/kafka-topics.sh  \
  --bootstrap-server my-cluster-kafka-bootstrap:9092  \
  --topic first_topic  \
  --describe

# kafka-console-producer
bin/kafka-console-producer.sh  \
  --bootstrap-server my-cluster-kafka-bootstrap:9092  \
  --topic first_topic  \
  --producer-property aks=all

# kafka-console-consumer
bin/kafka-console-consumer.sh  \
  --bootstrap-server my-cluster-kafka-bootstrap:9092  \
  --topic first_topic  \
  --from-beginning  \
  --group cg1

# kafka-consumer-groups
bin/kafka-consumer-groups.sh  \
  --bootstrap-server my-cluster-kafka-bootstrap:9092  \
  --list

bin/kafka-consumer-groups.sh  \
  --bootstrap-server my-cluster-kafka-bootstrap:9092  \
  --describe cg1
