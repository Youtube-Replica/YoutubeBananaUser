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
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class CreateUser extends ConcreteCommand {

    public void execute() throws NoSuchAlgorithmException {
        this.consume("u3");
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
        Envelope envelope = (Envelope) props.get("envelope");
        String response = User.signupUser(username,email,password);
//        String response = (String)props.get("body");
        sendMessage("database",properties.getCorrelationId(), response);

    }

    @Override
    public void handleApi(HashMap<String, Object> service_parameters) {
        HashMap<String, Object> props = parameters;
        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
        String serviceBody = service_parameters.get("body").toString();

        Envelope envelope = (Envelope) props.get("envelope");
        try {
            channel.basicPublish("", properties.getReplyTo(), replyProps, serviceBody.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
