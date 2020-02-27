package dev.fernandocarvalho;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    public static void main(String[] args) throws SQLException {
        CreateUserService createUserService = new CreateUserService();
        try (KafkaService<Order> kafkaService = new KafkaService<>(
                CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                Order.class,
                Map.of())) {
            kafkaService.run();
        }
    }

    public CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/user_database.db";
        this.connection = DriverManager.getConnection(url);
        connection.createStatement().execute("create table if not exists Users(" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");
    }

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("-----------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        Order order = record.value();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }
    }

    private void insertNewUser(String email) throws SQLException {
        PreparedStatement insert = connection.prepareStatement("insert into Users (uuid, email) values (?,?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("Usuario uuid e " + email + " adicionado!!");
    }

    private boolean isNewUser(String email) throws SQLException {
        PreparedStatement exists = connection.prepareStatement("select uuid from Users where email = ? limit 1");
        exists.setString(1, email);
        ResultSet results = exists.executeQuery();
        return !results.next();
    }

}
