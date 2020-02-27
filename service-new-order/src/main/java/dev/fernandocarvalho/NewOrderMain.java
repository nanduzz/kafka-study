package dev.fernandocarvalho;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {
            try (KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>()) {
                String email = Math.random() + "@email.com";
                for (int i = 0; i < 10; i++) {
                    String orderId = UUID.randomUUID().toString();
                    BigDecimal amount = BigDecimal.valueOf(Math.random() * 5000 + 1);

                    Order order = new Order(orderId, amount, email);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

                    String body = "Thank you for order! We are processing your order!";
                    String subject = "subject";
                    Email emailCode = new Email(subject, body);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
                }
            }
        }

    }
}
