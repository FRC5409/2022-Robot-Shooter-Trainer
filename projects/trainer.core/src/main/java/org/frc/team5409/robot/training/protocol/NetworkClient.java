package org.frc.team5409.robot.training.protocol;

import java.io.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class NetworkClient {
    private final NetworkSocket _socket;
    private final SendableContext _context;

    public NetworkClient(NetworkSocket socket, SendableContext context) {
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
                    
                    writer.write(transaction.getPayload());
                    writer.flush();
                } catch (IOException e) {
                    return new NetworkTransactionResult(NetworkStatus.STATUS_ERROR, null, e);
                }

                
                try {
                    DataInputStream stream = _socket.getInputStream();
                    SendableReader reader = new SendableReader(_context, stream);

                    int statusCode = stream.readInt();
                    NetworkStatus status = NetworkStatus.fromId(statusCode);

                    if (status == null)
                        throw new IOException("Unexpected status code 0x" + Integer.toHexString(statusCode));

                    NetworkSendable result = reader.read();

                    return new NetworkTransactionResult(status, result);
                } catch (IOException e) {
                    return new NetworkTransactionResult(NetworkStatus.STATUS_ERROR, null, e);
                }
            }
        );

        NetworkExecutors.getInstance().submit(transactionTask);
        return transactionTask;
    }
}
