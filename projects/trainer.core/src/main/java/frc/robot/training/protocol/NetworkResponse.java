package frc.robot.training.protocol;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class NetworkResponse {
    private final NetworkSendable _result;
    private final NetworkStatus _status;
    private final IOException _exception;

    public NetworkResponse(NetworkStatus status) {
        this(status, null, null);
    }

    public NetworkResponse(NetworkStatus status, @Nullable NetworkSendable result) {
        this(status, result, null);
    }

    public NetworkResponse(NetworkStatus status, @Nullable NetworkSendable result, @Nullable IOException e) {
        _status = status;
        _result = result;
        _exception = e;
    }

    public NetworkStatus getStatus() {
        return _status;
    }

    @Nullable
    public NetworkSendable getSendableResult() {
        return _result;
    }

    @Nullable
    public IOException getException() {
        return _exception;
    }
}
