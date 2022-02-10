package frc.robot.training.protocol;

import org.jetbrains.annotations.Nullable;

public class NetworkServerResponse {
    private final NetworkStatus _status;
    private final NetworkSendable _payload;

    public NetworkServerResponse(NetworkStatus status) {
        _status = status;
        _payload = null;
    }

    public NetworkServerResponse(NetworkStatus status, @Nullable NetworkSendable payload) {
        _status = status;
        _payload = payload;
    }

    public NetworkStatus getStatus() {
        return _status;
    }

    @Nullable
    public NetworkSendable getPayload() {
        return _payload;
    }
}
