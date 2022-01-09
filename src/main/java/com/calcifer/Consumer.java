package com.calcifer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;
import java.util.Arrays;
import java.time.Duration;

class Consumer {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Consumer.class.getName());
        String boostrapServers = "127.0.0.1:9092";
        String groupId = "my-first-application";
        String topicName = "test-topic";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // earliest, latest, none

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // subscription
        consumer.subscribe(Arrays.asList(topicName));

        // subscription
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> r : records) {
                logger.info("Key: {}\tValue: {}\tOffset: {}\tPartition: {}", r.key(), r.value(), r.offset(),
                        r.partition());
            }
        }

    }
}
