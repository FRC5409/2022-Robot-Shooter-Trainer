package org.frc.team5409.robot;

import java.net.ServerSocket;
import java.util.concurrent.Future;

import org.frc.team5409.robot.training.protocol.KeyValueSendable;
import org.frc.team5409.robot.training.protocol.NetworkRequest;
import org.frc.team5409.robot.training.protocol.NetworkServer;
import org.frc.team5409.robot.training.protocol.NetworkSocket;
import org.frc.team5409.robot.training.protocol.SendableContext;
import org.frc.team5409.robot.training.protocol.StringSendable;
import org.frc.team5409.robot.training.protocol.ValueSendable;

public class MainServer {
    public static void main(String args[]) {
        try {
            SendableContext context = new SendableContext();
              context.registerSendable(ValueSendable.WHAT, ValueSendable::new);
              context.registerSendable(KeyValueSendable.WHAT, KeyValueSendable::new);
              context.registerSendable(StringSendable.WHAT, StringSendable::new);
              
            ServerSocket serverSocket = new ServerSocket(5409);
            NetworkSocket connectionSocket = NetworkSocket.create(
                serverSocket.accept()
            );

            NetworkServer server = new NetworkServer(connectionSocket, context);
            Future<NetworkRequest> futureRequest = server.getRequest();
            NetworkRequest request = futureRequest.get();
            
            System.out.println("Got request " + request.getPayload());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
