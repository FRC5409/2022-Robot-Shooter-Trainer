package frc.robot.training.protocol;

public class NetworkTransaction {
    private final NetworkSendable _payload;

    public NetworkTransaction(NetworkSendable payload) {
        _payload = payload;
    }

    public NetworkSendable getPayload() {
        return _payload;
    }
}
