package commands;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import commands.Command;
import model.User;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class RetrieveUser extends ConcreteCommand {

    public void execute() throws NoSuchAlgorithmException {
        this.consume("r1");
        HashMap<String, Object> props = parameters;
        String password = "";
        String email = "";
        Channel channel = (Channel) props.get("channel");
        JSONParser parser = new JSONParser();
        int id = 0;
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

        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
        Envelope envelope = (Envelope) props.get("envelope");
        String response = "";
        if(!login){
        response = User.getUserById(id);
        }else{
            response = User.getUserSalt(email);
        }

        sendMessage("database",properties.getCorrelationId(), response);

    }


    @Override
    public void handleApi(HashMap<String, Object> service_parameters) {
        HashMap<String, Object> props = parameters;
        AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
        AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");


        Envelope envelope = (Envelope) props.get("envelope");
        try {
            JSONParser parser = new JSONParser();
            JSONArray serviceBody = (JSONArray) parser.parse((String) service_parameters.get("body"));
            if(serviceBody !=null){
                JSONObject message = (JSONObject) serviceBody.get(0);
            if(!message.containsKey("salt")){
            // TODO Re-map al UUID to old replyTo
            channel.basicPublish("", properties.getReplyTo(), replyProps, serviceBody.toString().getBytes("UTF-8"));
            }else{
                JSONObject serverBody = (JSONObject) parser.parse((String) props.get("body"));
                JSONObject params = (JSONObject) parser.parse(serverBody.get("parameters").toString());
                String email = params.get("email").toString();
                String password = params.get("password").toString();
                String salt = message.get("salt").toString();
                String response =  User.loginUser(email,password,salt);
                System.out.println(response);
                sendMessage("database",properties.getCorrelationId(), response);
            }
            }
//            channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
