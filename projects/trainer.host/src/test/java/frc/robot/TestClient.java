package frc.robot;

import java.io.IOException;
import java.util.Random;

import frc.robot.training.protocol.NetworkClient;
import frc.robot.training.protocol.NetworkSendable;
import frc.robot.training.protocol.NetworkSocket;
import frc.robot.training.protocol.NetworkRequest;
import frc.robot.training.protocol.NetworkResponse;
import frc.robot.training.protocol.SendableContext;
import frc.robot.training.protocol.generic.KeyValueSendable;
import frc.robot.training.protocol.generic.StringSendable;
import frc.robot.training.protocol.generic.ValueSendable;

public class TestClient {
    public static void main(String[] args) {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void execute() throws IOException {
        SendableContext context = new SendableContext();
        context.registerSendable(KeyValueSendable.class);
        context.registerSendable(StringSendable.class);
        context.registerSendable(ValueSendable.class);

        NetworkSocket socket = NetworkSocket.create();
        NetworkClient client = new NetworkClient(socket, context);

        double a = 1.05412e-3;
        double b = 25;

        double v1 = 25*25*a + b;
        double v2 = 0*0*a + b;

        final double max = Math.max(v1,v2);
        final double min = Math.min(v1,v2);

        for (int i = 0; i < 1000; i++) {
            KeyValueSendable out = new KeyValueSendable();
            out.putSendable("trainer.topic", new StringSendable("trainer:submitData"));

            double distance = random(0, 25);
            double speed = (distance*distance* a + b);

            out.putDouble("trainer.data.distance", distance/25);
            out.putDouble("trainer.data.speed", (speed-min)/(max-min));

            send(client, out);
        }
        client.close();
    }

    private static void send(NetworkClient client, NetworkSendable payload) throws IOException {
        System.out.println("Sent " + payload);
        NetworkResponse result = client.submitRequest(new NetworkRequest(payload));

        System.out.println("Got status " + result.getStatus());
        if (result.getSendableResult() != null) {
            System.out.println("Got " + result.getSendableResult());
        } else {
            System.out.println("Got no result");
        }
    }

    private static final Random rnd = new Random();
    private static double random(double min, double max) {
        return (min+(max-min)*rnd.nextDouble());
    }
}
