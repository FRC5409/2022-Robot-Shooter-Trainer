package frc.robot.training.protocol;

import frc.robot.training.protocol.NetworkSendable;
import frc.robot.training.protocol.NetworkStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class NetworkTransactionResult {
    private final NetworkSendable _result;
    private final NetworkStatus _status;
    private final IOException _exception;

    public NetworkTransactionResult(NetworkStatus status, @Nullable NetworkSendable result) {
        _status = status;
        _result = result;
        _exception = null;
    }

    public NetworkTransactionResult(NetworkStatus status, @Nullable NetworkSendable result, IOException e) {
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