package com.calcifer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerWithKeys {
    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger(ProducerWithCallback.class);

        String boostrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // send data
        Callback callback = new Callback() {
            public void onCompletion(RecordMetadata m, Exception e) {
                if (e == null) {
                    logger.info("Record Metadata - Topic: {}\tPartition: {}\tOffset: {}\tTimestamp: {}", m.topic(),
                            m.partition(), m.offset(), m.timestamp());
                } else {
                    logger.error("Error while publishing");
                }
            }
        };

        String topic = "test-topic";

        for (int i = 0; i < 10; i++) {
            String value = "hello world " + Integer.toString(i);
            String key = "id_" + Integer.toString(i);
            logger.info("Key: " + key);
            ProducerRecord<String, String> record = new ProducerRecord(topic, key, value);
            producer.send(record, callback);
            producer.flush();
        }

        producer.close();

    }
}
