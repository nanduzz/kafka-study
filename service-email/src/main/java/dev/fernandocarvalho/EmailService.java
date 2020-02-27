package dev.fernandocarvalho;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public class EmailService {

    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        KafkaService<Email> service = new KafkaService<>(EmailService.class.getSimpleName(),
                "ECOMMERCE_SEND_EMAIL",
                emailService::parse,
                Email.class,
                Map.of());
        service.run();
    }

    private void parse(ConsumerRecord<String, Email> record) {
        System.out.println("-----------------------------------------------");
        System.out.println("Sending Email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
