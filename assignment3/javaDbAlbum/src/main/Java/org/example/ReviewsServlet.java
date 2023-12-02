package org.example;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ReviewsServlet", value = "/review/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,    // 10 MB
        maxFileSize = 1024 * 1024 * 50,        // 50 MB
        maxRequestSize = 1024 * 1024 * 100)
public class ReviewsServlet extends HttpServlet {

    private final ConnectionFactory factory = new ConnectionFactory();

    private final static String queueName = "album_like";

    {
        factory.setHost("localhost");
    }

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            String urlPath = request.getPathInfo();
            String albumId = urlPath.split("/")[1];
            Integer action = Integer.valueOf(urlPath.split("/")[2]);
            MqMessage message = new MqMessage();
            message.setId(albumId);
            message.setAction(action);
            String content = gson.toJson(message);
            sendMq(content);
            PrintWriter out = response.getWriter();
            response.setCharacterEncoding("UTF-8");
            out.print(content);
            out.flush();
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendMq(String content){
        if("".equals(content) || content == null){
            return false;
        }
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, content.getBytes("UTF-8"));
            return true;
        }catch (Exception ignored){

        }
        return false;
    }

}
