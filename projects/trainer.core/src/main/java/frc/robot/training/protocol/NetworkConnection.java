package frc.robot.training.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class NetworkConnection {
    private final NetworkSocket   _socket;
    private final SendableContext _context;

    public NetworkConnection(NetworkSocket socket, SendableContext context) {
        _socket = socket;
        _context = context;
    }

    public NetworkServerRequest getRequest() throws IOException {
        SendableReader reader = new SendableReader(_context, _socket.getInputStream());
        
        try {
            NetworkSendable payload = reader.read();
            return new NetworkServerRequest(this, NetworkStatus.STATUS_OK, payload);
        } catch (IOException e) {
            return new NetworkServerRequest(this, NetworkStatus.STATUS_INTERRUPTED);
        }
    }

    public Future<NetworkServerRequest> getRequestAsync() {
        FutureTask<NetworkServerRequest> task = new FutureTask<>(this::getRequest);
        NetworkExecutors.getInstance().submit(task);
        return task;
    }

    void submitResponse(NetworkServerResponse response) throws IOException {
        DataOutputStream stream = _socket.getOutputStream();

        NetworkSendable payload = response.getPayload();

        // get response header flags
        int flags = NetworkResponseHeader.FLAG_NULL;

        // enable sendable payload flag (if payload exists)
        if (payload != null)
            flags |= NetworkResponseHeader.FLAG_SENDABLE_PAYLOAD;

        // write response header
        NetworkResponseHeader header = new NetworkResponseHeader(response.getStatus(), flags);
        stream.writeInt(NetworkResponseHeader.encode(header));

        // write payload (if payload exists)
        if (payload != null) {
            SendableWriter writer = new SendableWriter(_context, stream);
            writer.write(payload);
        }

        stream.flush();
    }

    Future<Void> submitResponseAsync(NetworkServerResponse response) {
        FutureTask<Void> task = new FutureTask<>(
            () -> {
                submitResponse(response);
                return null;
            }
        );

        NetworkExecutors.getInstance().submit(task);
        return task;
    }

    public NetworkSocket getSocket() {
        return _socket;
    }

    public boolean isConnected() {
        return _socket.isConnected();
    }

    public void close() throws IOException {
        _socket.close();
    }
}
