package dev.fernandocarvalho;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    private final KafkaDispatcher<Order> orderKafkaDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        FraudDetectorService fraudDetectorService = new FraudDetectorService();
        try (KafkaService<Order> kafkaService = new KafkaService<>(
                FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudDetectorService::parse,
                Order.class,
                Map.of())) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("-----------------------------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Order order = record.value();
        if (isFraud(order)) {
            //pretending that the fraud happens when the amount is >= 4500
            System.out.println("Order is a fraud!!!!");
            orderKafkaDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), order);
        } else {
            System.out.println("Approved: " + order);
            orderKafkaDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), order);
        }
    }


    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4000")) >= 0;
    }

}
