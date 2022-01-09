package com.calcifer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.errors.WakeupException;

import java.util.Properties;
import java.util.Arrays;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

class ConsumerWithThreads {
    public static void main(String[] args) {
        new ConsumerWithThreads().run();
    }

    private ConsumerWithThreads() {
    }

    private void run() {
        Logger logger = LoggerFactory.getLogger(Consumer.class.getName());
        String boostrapServers = "127.0.0.1:9092";
        String groupId = "app2";
        String topicName = "test-topic";
        CountDownLatch latch = new CountDownLatch(1);

        // create the consumer runnable
        logger.info("Creating the consumer thread");
        Runnable myConsumerRunnable = new ConsumerRunnable(boostrapServers, topicName, groupId, latch);
        Thread myThread = new Thread(myConsumerRunnable);
        myThread.start();

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook");
            ((ConsumerRunnable) myConsumerRunnable).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Application has exited");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application got interrupted", e);
        } finally {
            logger.info("Application is closing");
        }
    }

    public class ConsumerRunnable implements Runnable {

        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;
        private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());

        public ConsumerRunnable(String boostrapServers, String topicName, String groupId, CountDownLatch latch) {
            this.latch = latch;
            // create consumer
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // earliest, latest, none
            consumer = new KafkaConsumer<String, String>(properties);
            consumer.subscribe(Arrays.asList(topicName));

        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                    for (ConsumerRecord<String, String> r : records) {
                        logger.info("Key: {}\tValue: {}\tOffset: {}\tPartition: {}", r.key(), r.value(), r.offset(), r.partition());
                    }
                }
            } catch (WakeupException e) {
                logger.info("Received shutdown signal");
            } finally {
                consumer.close();
                // tell our main code we're done with the consumer
                latch.countDown();
            }
        }

        public void shutdown() {
            // wakeup() is a special method to interrupt consumer.poll()
            consumer.wakeup();
        }
    }

}
