package org.example;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Consumer {

    public static void main(String[] args) {
        final Gson gson = new Gson();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String QUEUE_NAME = "album";
        MongoTool mongoTool = new MongoTool();
        int count = 0;
        try (
            Connection connection = factory.newConnection();
        ) {
            List<Channel> channelList = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                channelList.add(connection.createChannel());
            }
            while (true){
                Channel channel = channelList.get(count % 10);
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
                count++;
            }
        }catch (Exception ignored){

        }
    }
}
