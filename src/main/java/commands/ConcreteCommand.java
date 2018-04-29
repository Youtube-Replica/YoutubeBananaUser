package commands;

import com.rabbitmq.client.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public abstract class ConcreteCommand extends Command {
    Channel channel;
    String RPC_QUEUE_NAME;
    public void consume(String RPC_QUEUE_NAME) {

        this.RPC_QUEUE_NAME = RPC_QUEUE_NAME;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting DB-RPC Responses");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties myProps = (AMQP.BasicProperties) parameters.get("properties");
                    if(properties.getCorrelationId().equals(myProps.getCorrelationId())){
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(properties.getCorrelationId())
                            .build();
                    System.out.println("Responding to db-corrID: "+ properties.getCorrelationId());

                    String response = "";

                    try {
                        String message = new String(body, "UTF-8");
                        HashMap<String, Object> props = new HashMap<String, Object>();
                        props.put("channel", channel);
                        props.put("properties", properties);
                        props.put("replyProps", replyProps);
                        props.put("envelope", envelope);
                        props.put("body", message);

                        handleApi(props);
                    } catch (RuntimeException e) {
                        System.out.println(" [.] " + e.toString());
                    }  finally {
                        synchronized (this) {
                            this.notify();
                        }
                    }
                } else {
                        this.getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    public abstract void handleApi(HashMap<String, Object> service_parameters);

    public void sendMessage(String service, String requestId, String message){
        try {
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(requestId)
                    .replyTo(RPC_QUEUE_NAME)
                    .build();
            System.out.println("Sent: "+ message);
//            Envelope envelope = (Envelope) parameters.get("envelope");
            channel.basicPublish("", service + "-request", props, message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
