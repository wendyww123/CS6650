package org.example;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;

public class Consumer {

    public static void main(String[] args) {
        final Gson gson = new Gson();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String QUEUE_NAME = "album_like";
        MongoTool mongoTool = new MongoTool();
        try (
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()
        ) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicConsume(QUEUE_NAME, true, (tag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    MqMessage mqMessage = gson.fromJson(message, MqMessage.class);
                    mongoTool.saveMsg(mqMessage);
                } catch (Exception e) {
                    e.getMessage();
                }
            }, callback -> { });
        }catch (Exception ignored){

        }
    }
}
