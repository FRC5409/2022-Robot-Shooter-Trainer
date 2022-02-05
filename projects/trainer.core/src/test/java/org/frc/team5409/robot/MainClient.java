package org.frc.team5409.robot;

import java.util.concurrent.Future;

import org.frc.team5409.robot.training.protocol.*;
import org.frc.team5409.robot.training.protocol.generic.KeyValueSendable;
import org.frc.team5409.robot.training.protocol.generic.StringSendable;
import org.frc.team5409.robot.training.protocol.generic.ValueSendable;

public class MainClient {
    public static void main(String[] args) {
        try {
            SendableContext context = new SendableContext();
            context.registerSendable(ValueSendable.class);
            context.registerSendable(KeyValueSendable.class);
            context.registerSendable(StringSendable.class);

            NetworkSocket socket = NetworkSocket.create("localhost");

            NetworkClient client = new NetworkClient(socket, context);

            Future<NetworkTransactionResult> futureResult = client.submitTransaction(
                new NetworkTransaction(
                    new StringSendable("Hello Wxrld!")
                )
            );

            System.out.println("Sent transaction");

            NetworkTransactionResult result = futureResult.get();
            System.out.println("Received result with status : " + result.getStatus());

            NetworkSendable resultSendable = result.getSendableResult();
            if (resultSendable == null)
                System.out.println("Result has no sendable payload.");
            else
                System.out.println("Receieved sendable result " + resultSendable);

            socket.close();
            System.out.println("Closed socket");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
