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

public class RetrieveUser extends ConcreteCommand {

    public void execute() throws NoSuchAlgorithmException {
        this.consume("r1");
        HashMap<String, Object> props = parameters;

        Channel channel = (Channel) props.get("channel");
        JSONParser parser = new JSONParser();
        int id = 0;
        String email = "";
        String password = "";
        boolean login = false;
        System.out.println("here2?");
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
          catch (Exception e1){
              System.out.println("ah fe moshkela");
        }
        System.out.println("here?");
        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
        Envelope envelope = (Envelope) props.get("envelope");
        String response = "";
        if(!login){
        response = User.getUserById(id);
        }else{
            response = User.loginUser(email,password);
        }

        sendMessage("database",properties.getCorrelationId(), response);

    }


    @Override
    public void handleApi(HashMap<String, Object> service_parameters) {
        System.out.println("Replying to the server..");
        HashMap<String, Object> props = parameters;
        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
        String body = service_parameters.get("body").toString();
        Envelope envelope = (Envelope) props.get("envelope");
        try {
            // TODO Re-map al UUID to old replyTo
            channel.basicPublish("", properties.getReplyTo(), replyProps, body.getBytes("UTF-8"));
//            channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
