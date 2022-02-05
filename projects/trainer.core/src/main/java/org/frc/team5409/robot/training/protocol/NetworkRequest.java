package org.frc.team5409.robot.training.protocol;

import java.io.IOException;

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
}
