package frc.robot.training.protocol;

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

    public NetworkResponse submitRequest(NetworkRequest request) throws IOException {
        if (_socket.isClosed())
            return new NetworkResponse(NetworkStatus.STATUS_UNAVAILABLE, null);

        // write request payload
        SendableWriter writer = new SendableWriter(_context, _socket.getOutputStream());
        writer.write(request.getPayload());
        writer.flush();

        // read response header
        DataInputStream inputStream = _socket.getInputStream();
        NetworkResponseHeader header = NetworkResponseHeader.decode(inputStream.readInt());

        // check if request response contains payload
        if (header.hasFlag(NetworkResponseHeader.FLAG_SENDABLE_PAYLOAD)) {
            SendableReader reader = new SendableReader(_context, inputStream);
            return new NetworkResponse(header.status, reader.read());
        } else {
            return new NetworkResponse(header.status);
        }
    }

    public Future<NetworkResponse> submitRequestAsync(NetworkRequest request) {
        FutureTask<NetworkResponse> task = new FutureTask<>(() -> submitRequest(request));
        NetworkExecutors.getInstance().submit(task);
        return task;
    }

    public void close() throws IOException {
        _socket.close();
    }
}
