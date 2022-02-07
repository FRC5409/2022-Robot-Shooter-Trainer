package frc.robot.training;

import java.io.IOException;
import java.net.ServerSocket;

import frc.robot.training.protocol.*;
import frc.robot.training.protocol.generic.KeyValueSendable;
import frc.robot.training.protocol.generic.StringSendable;
import frc.robot.training.protocol.generic.ValueSendable;

public class TrainingServer {
    private NetworkConnection _connection;
    private ServerSocket _socket;

    public TrainingServer() {
        _connection = null;
        _socket = null;
    }

    public void initialize(ServerConfiguration configuration) throws IOException {
        SendableContext context = new SendableContext();
            context.registerSendable(ValueSendable.class);
            context.registerSendable(KeyValueSendable.class);
            context.registerSendable(StringSendable.class);

        _socket = new ServerSocket(configuration.port);

        // wait for connection
        NetworkSocket connectionSocket = NetworkSocket.create(_socket.accept());
        _connection = new NetworkConnection(connectionSocket, context);
    }

    public void run() throws IOException {
        while (_connection.isConnected()) {
            NetworkRequest request = _connection.getRequest();
                    
            KeyValueSendable payload = (KeyValueSendable) request.getPayload();
            System.out.println("Got request " + payload);

            StringSendable topic = (StringSendable) payload.getSendable("trainer.topic");
            if (topic.getValue().equals("trainer.get-model")) {
                
            }

            payload.putSendable("trainer.topic", new StringSendable("trainer.get-model"));
            

            NetworkRequestResult result = new NetworkRequestResult(NetworkStatus.STATUS_OK);
            request.fulfill(result);
        }
    }

    public void close() throws IOException {
        _connection.close();
        _socket.close();
    }
}
