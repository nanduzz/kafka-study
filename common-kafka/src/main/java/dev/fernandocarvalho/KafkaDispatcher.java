package dev.fernandocarvalho;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, T> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }


    private static Properties properties() {

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");

        return properties;
    }

    void send(String topic, String key, T value) throws ExecutionException, InterruptedException {
        ProducerRecord<String, T> record = new ProducerRecord<>(topic, key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            logData(data);
        };

        producer.send(record, callback).get();
    }


    private void logData(RecordMetadata data) {
        StringBuilder sb = new StringBuilder()
                .append("sucesso enviando ")
                .append(data.topic())
                .append(":::partition ")
                .append(data.partition())
                .append(":::offset ")
                .append(data.offset());
        System.out.println(sb.toString());
    }

    @Override
    public void close() {
        producer.close();
    }
}
