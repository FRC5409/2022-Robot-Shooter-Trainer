package org.frc.team5409.robot;

import java.net.ServerSocket;
import java.util.concurrent.Future;

import org.frc.team5409.robot.training.protocol.*;
import org.frc.team5409.robot.training.protocol.generic.KeyValueSendable;
import org.frc.team5409.robot.training.protocol.generic.StringSendable;
import org.frc.team5409.robot.training.protocol.generic.ValueSendable;

public class MainServer {
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
                System.out.println("Got request " + request.getPayload());

                NetworkRequestResult result = new NetworkRequestResult(NetworkStatus.STATUS_OK);
                request.fulfill(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
