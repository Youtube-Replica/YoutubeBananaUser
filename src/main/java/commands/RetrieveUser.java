package commands;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import commands.Command;
import model.User;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class RetrieveUser extends Command {

    public void execute() throws NoSuchAlgorithmException {
        HashMap<String, Object> props = parameters;

        Channel channel = (Channel) props.get("channel");
        JSONParser parser = new JSONParser();
        int id = 0;
        String email = "";
        String password = "";
        boolean login = false;
        try {
            JSONObject body = (JSONObject) parser.parse((String) props.get("body"));
            System.out.println(body.toString());
            JSONObject params = (JSONObject) parser.parse(body.get("parameters").toString());
            if(params.containsKey("id")){
            id = Integer.parseInt(params.get("id").toString());
            }else{
                email = (String) params.get("email");
                password = (String) params.get("password");
                login = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
        Envelope envelope = (Envelope) props.get("envelope");
        String response = "";
        if(!login){
        response = User.getUserById(id);
        }else{
            response = User.loginUser(email,password);
            System.out.println(response + "whaaat");
        }
//        String response = (String)props.get("body");
        try {
            channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
            channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
