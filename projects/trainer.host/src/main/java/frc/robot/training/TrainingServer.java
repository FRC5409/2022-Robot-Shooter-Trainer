package frc.robot.training;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

import org.jetbrains.annotations.Nullable;

import frc.robot.training.protocol.*;
import frc.robot.training.protocol.generic.KeyValueSendable;
import frc.robot.training.protocol.generic.StringSendable;
import frc.robot.training.protocol.generic.ValueSendable;

public class TrainingServer {
    private final Logger _logger;

    private NetworkConnection _connection;
    private ServerSocket _socket;

    private TrainerExecutor _executor;
    private TrainingStorage _storage;
    
    @Nullable
    private ModelParameters _model;
    private int _parametersSize;
    
    public TrainingServer() {
        _logger = Logger.getLogger("Server");
    }

    public void initialize(ServerConfiguration configuration) throws IOException {
        SendableContext context = new SendableContext();
            context.registerSendable(ValueSendable.class);
            context.registerSendable(KeyValueSendable.class);
            context.registerSendable(StringSendable.class);

        _executor = new TrainerExecutor(
            new File(configuration.trainerExecutableFile));

        _storage = new TrainingStorage(
            new StorageFile(configuration.modelParametersFile),
            new StorageFile(configuration.dataStorageFile));

        _parametersSize = configuration.modelParametersSize;
        _model = _storage.getModelParameters(_parametersSize);

        _socket = new ServerSocket(configuration.port);

        // wait for connection
        NetworkSocket connectionSocket = NetworkSocket.create(_socket.accept());
        _connection = new NetworkConnection(connectionSocket, context);
    }

    public void run() throws IOException {
        while (_connection.isConnected()) {
            NetworkRequest request = _connection.getRequest();
                    
            KeyValueSendable payload = (KeyValueSendable) request.getPayload();
            System.out.println("Got request " + payload);

            StringSendable topic = (StringSendable) payload.getSendable("trainer.topic");
            if (topic.getValue().equals("trainer.get-model")) {
                requestModel(request);
            }

            payload.putSendable("trainer.topic", new StringSendable("trainer.get-model"));
            

        }
    }

    public void close() throws IOException {
        _connection.close();
        _socket.close();
    }

    private void requestModel(NetworkRequest request) throws IOException {
        NetworkStatus status;
        KeyValueSendable out = new KeyValueSendable();

        if (_model == null) {
            _logger.warning("Recieved request for model information, when model was not loaded.");
            out.putSendable("reason", new StringSendable("Model does not exist."));
            status = NetworkStatus.STATUS_ERROR;
        } else {
            out.putInteger("trainer.model.size", _parametersSize);

            for (int i = 0; i < _parametersSize; i++) {
                out.putDouble("trainer.model.parameters["+i+"]", _model.get(i));
            }

            status = NetworkStatus.STATUS_OK;
        }

        request.fulfill(new NetworkRequestResult(status, out));
    }
    

    private void processData(NetworkRequest request) throws IOException {
        KeyValueSendable payload = (KeyValueSendable) request.getPayload();

        NetworkStatus status;
        KeyValueSendable out = new KeyValueSendable();

        try {
            double speed = payload.getDouble("trainer.data.speed");
            double distance = payload.getDouble("trainer.data.distance");

            _logger.info("Recevied values:\nspeed    = " + speed + "\ndistance = " + distance);

            _logger.info("Training model...");
            
            _storage.writeTrainingData(new TrainingData(speed, distance));
            _executor.train();
            _model = _storage.getModelParameters(_parametersSize);

            _logger.info("Finished training model. Got parameters:\n" + _model);
            
            out.putInteger("trainer.model.size", _parametersSize);
            for (int i = 0; i < _parametersSize; i++) {
                out.putDouble("trainer.model.parameters["+i+"]", _model.get(i));
            }

            status = NetworkStatus.STATUS_OK;

        } catch (Exception e) {
            _logger.throwing("TrainingServer", "processData", e);
            out.putSendable("reason", new StringSendable("Encountered unexpected exception."));
            status = NetworkStatus.STATUS_ERROR;
        }

        request.fulfill(new NetworkRequestResult(status, out));
    }
}
