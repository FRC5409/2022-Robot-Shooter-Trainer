package frc.robot.training.protocol;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import frc.robot.training.protocol.NetworkRequestResult;
import frc.robot.training.protocol.NetworkSendable;
import frc.robot.training.protocol.NetworkServer;

public class NetworkRequest {
    private final NetworkServer _server;
    private final NetworkSendable _payload;

    public NetworkRequest(NetworkServer server, NetworkSendable payload) {
        _server = server;
        _payload = payload;
    }
    
    public NetworkSendable getPayload() {
        return _payload;
    }

    public void fulfill(NetworkRequestResult result) throws IOException {
        _server.fulfillRequest(this, result);
    }

    public Future<Void> fulfillAsync(NetworkRequestResult result) {
        return _server.fulfillRequestAsync(this, result);
    }
}
