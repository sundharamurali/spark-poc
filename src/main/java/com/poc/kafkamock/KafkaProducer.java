package com.poc.kafkamock;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

public class KafkaProducer {

    private final Producer<String, String> producer;

    public KafkaProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    public Future<RecordMetadata> send(String key, String value) {
        ProducerRecord record = new ProducerRecord("topic_sports_news", key, value);
        return producer.send(record);
    }
}
