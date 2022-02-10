package frc.robot.training.protocol;

import java.io.IOException;
import java.util.concurrent.Future;

import org.jetbrains.annotations.Nullable;

public class NetworkServerRequest {
    private final NetworkConnection _connection;
    private final NetworkSendable _payload;
    private final NetworkStatus _status;

    public NetworkServerRequest(NetworkConnection connection, NetworkStatus status) {
        this(connection, status, null);
    }

    public NetworkServerRequest(NetworkConnection connection, NetworkStatus status, @Nullable NetworkSendable payload) {
        _connection = connection;
        _payload = payload;
        _status = status;
    }
    
    @Nullable
    public NetworkSendable getPayload() {
        return _payload;
    }

    public NetworkStatus getStatus() {
        return _status;
    }

    public void fulfill(NetworkServerResponse response) throws IOException {
        _connection.submitResponse(response);
    }

    public Future<Void> fulfillAsync(NetworkServerResponse response) {
        return _connection.submitResponseAsync(response);
    }
}
