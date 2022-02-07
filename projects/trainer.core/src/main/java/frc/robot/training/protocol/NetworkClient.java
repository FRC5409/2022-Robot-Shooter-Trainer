package frc.robot.training.protocol;

import java.io.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import frc.robot.training.protocol.NetworkExecutors;
import frc.robot.training.protocol.NetworkSendable;
import frc.robot.training.protocol.NetworkSocket;
import frc.robot.training.protocol.NetworkStatus;
import frc.robot.training.protocol.NetworkTransaction;
import frc.robot.training.protocol.NetworkTransactionResult;
import frc.robot.training.protocol.SendableContext;
import frc.robot.training.protocol.SendableReader;
import frc.robot.training.protocol.SendableWriter;

public class NetworkClient {
    private final NetworkSocket _socket;
    private final SendableContext _context;

    public NetworkClient(NetworkSocket socket, SendableContext context) {
        _socket = socket;
        _context = context;
    }

    public NetworkTransactionResult submitTransaction(NetworkTransaction transaction) {
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

    public Future<NetworkTransactionResult> submitTransactionAsync(NetworkTransaction transaction) {
        FutureTask<NetworkTransactionResult> task = new FutureTask<>(() -> submitTransaction(transaction));
        NetworkExecutors.getInstance().submit(task);
        return task;
    }
}