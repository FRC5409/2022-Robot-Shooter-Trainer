package org.frc.team5409.robot.training.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class NetworkServer {
    private final NetworkSocket   _socket;
    private final SendableContext _context;

    public NetworkServer(NetworkSocket socket, SendableContext context) {
        _socket = socket;
        _context = context;
    }


    public Future<NetworkRequest> getRequest() {
        FutureTask<NetworkRequest> requestTask = new FutureTask<>(
            () -> {
                SendableReader reader = new SendableReader(_context, _socket.getInputStream());
                return new NetworkRequest(this, reader.read());
            }
        );

        NetworkExecutors.getInstance().submit(requestTask);
        return requestTask;
    }

    Future<Void> fulfillRequest(NetworkRequest request, NetworkRequestResult result) throws IOException {
        FutureTask<Void> fufillmentTask = new FutureTask<>(
            () -> {
                DataOutputStream stream = _socket.getOutputStream();
                SendableWriter writer = new SendableWriter(_context, stream);

                stream.writeInt(result.getStatus().id());
                writer.write(result.getPayload());

                stream.flush();

                return null;
            }
        );

        NetworkExecutors.getInstance().submit(fufillmentTask);
        return fufillmentTask;
    }
}
