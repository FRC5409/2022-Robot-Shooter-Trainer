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


    public NetworkRequest getRequest() throws IOException {
        SendableReader reader = new SendableReader(_context, _socket.getInputStream());
        return new NetworkRequest(this, reader.read());
    }

    public Future<NetworkRequest> getRequestAsync() {
        FutureTask<NetworkRequest> task = new FutureTask<>(this::getRequest);
        NetworkExecutors.getInstance().submit(task);
        return task;
    }

    void fulfillRequest(NetworkRequest request, NetworkRequestResult result) throws IOException {
        DataOutputStream stream = _socket.getOutputStream();
        SendableWriter writer = new SendableWriter(_context, stream);
        NetworkSendable payload = request.getPayload();

        stream.writeInt(result.getStatus().id());

        if (payload != null)
            writer.write(payload);

        stream.flush();
    }

    public Future<Void> fulfillRequestAsync(NetworkRequest request, NetworkRequestResult result) {
        FutureTask<Void> task = new FutureTask<>(
            () -> {
                fulfillRequest(request, result);
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
