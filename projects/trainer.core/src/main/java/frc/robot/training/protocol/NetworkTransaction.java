package frc.robot.training.protocol;

import frc.robot.training.protocol.NetworkSendable;

public class NetworkTransaction {
    private final NetworkSendable _payload;

    public NetworkTransaction(NetworkSendable payload) {
        _payload = payload;
    }

    public NetworkSendable getPayload() {
        return _payload;
    }
}
