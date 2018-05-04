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

public class UpdateUser extends ConcreteCommand {

    public void execute() throws NoSuchAlgorithmException {
        HashMap<String, Object> props = parameters;
        this.consume("u4");
        Channel channel = (Channel) props.get("channel");
        JSONParser parser = new JSONParser();
        int id = 0;
        String password = "";
        try {
            //Check body, what values should be updated? password? username? etc. Call the correct corresponding function.
            JSONObject body = (JSONObject) parser.parse((String) props.get("body"));
//            System.out.println("The BODY is: " + body.toString());
            JSONObject params = (JSONObject) parser.parse(body.get("body").toString());
            id = Integer.parseInt(params.get("id").toString());
            password = params.get("password").toString();
            System.out.println("UserId: " + id + ", password: " + password);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
        Envelope envelope = (Envelope) props.get("envelope");
        String response = User.changePasswordById(id,password) + "";
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
