package commands;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import commands.Command;
import model.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;

public class CreateUser extends Command {

    public void execute() {
        HashMap<String, Object> props = parameters;

        Channel channel = (Channel) props.get("channel");
        JSONParser parser = new JSONParser();
        int id = 0;
        String username = "";
        String email = "";
        String password = "";
        try {
            JSONObject body = (JSONObject) parser.parse((String) props.get("body"));
//            System.out.println("The BODY is: " + body.toString());
            JSONObject params = (JSONObject) parser.parse(body.get("body").toString());
            username = params.get("username").toString();
            email = params.get("email").toString();
            password = params.get("password").toString();
            System.out.println("User: " + username + ", email: " + email + ", password: " + password);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
        Envelope envelope = (Envelope) props.get("envelope");
        String response = User.signupUser(username,email,password);
//        String response = (String)props.get("body");
        try {
            channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
            channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
