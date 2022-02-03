package org.frc.team5409.robot.training.protocol;

import java.io.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class NetworkBroker {
    private final NetworkSocket _socket;
    private final SendableContext _context;

    public NetworkBroker(NetworkSocket socket, SendableContext context) {
        _socket = socket;
        _context = context;
    }

    public Future<NetworkTransactionResult> submitTransaction(NetworkTransaction transaction) {
        FutureTask<NetworkTransactionResult> transactionTask = new FutureTask<>(
            () -> {
                if (_socket.isClosed())
                    return new NetworkTransactionResult(NetworkStatus.STATUS_UNAVAILABLE, null);

                try {
                    SendableWriter writer = new SendableWriter(_context, _socket.getOutputStream());
                    SendableReader reader = new SendableReader(_context, _socket.getInputStream());

                    writer.write(transaction.getPayload());

                    NetworkSendable result = reader.read();
                    return new NetworkTransactionResult(NetworkStatus.STATUS_OK, result);
                } catch (IOException e) {
                    return new NetworkTransactionResult(NetworkStatus.STATUS_ERROR, null, e);
                }
            }
        );

        NetworkExecutors.getInstance().submit(transactionTask);
        return transactionTask;
    }

}
