package frc.robot.training.protocol;

public class NetworkRequest {
    private final NetworkSendable _payload;

    public NetworkRequest(NetworkSendable payload) {
        _payload = payload;
    }

    public NetworkSendable getPayload() {
        return _payload;
    }
}
