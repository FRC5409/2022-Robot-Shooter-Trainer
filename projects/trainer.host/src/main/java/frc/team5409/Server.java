package frc.team5409;

import java.net.ServerSocket;

import frc.robot.training.protocol.*;
import frc.robot.training.protocol.generic.KeyValueSendable;
import frc.robot.training.protocol.generic.StringSendable;
import frc.robot.training.protocol.generic.ValueSendable;

public class Server {
    public static void main(String[] args) {
        try {
            SendableContext context = new SendableContext();
                context.registerSendable(ValueSendable.class);
                context.registerSendable(KeyValueSendable.class);
                context.registerSendable(StringSendable.class);

            ServerSocket serverSocket = new ServerSocket(5409);
            NetworkSocket connectionSocket = NetworkSocket.create(
                serverSocket.accept()
            );

            NetworkServer server = new NetworkServer(connectionSocket, context);
            while (connectionSocket.isConnected()) {
                NetworkRequest request = server.getRequest();
                
                KeyValueSendable payload = (KeyValueSendable) request.getPayload();
                System.out.println("Got request " + payload);

                StringSendable topic = (StringSendable) payload.getSendable("trainer.topic");
                if (topic.getValue().equals("trainer.get-model")) {
                    
                }

                payload.putSendable("trainer.topic", new StringSendable("trainer.get-model"));
                

                NetworkRequestResult result = new NetworkRequestResult(NetworkStatus.STATUS_OK);
                request.fulfill(result);
            }

            connectionSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
