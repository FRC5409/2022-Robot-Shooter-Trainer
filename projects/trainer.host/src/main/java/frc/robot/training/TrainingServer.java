package frc.robot.training;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
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
    private TrainerExecutionArguments _trainerArguments;

    private TrainingStorage _storage;

    @Nullable
    private ModelParameters _model;
    private int _parametersSize;

    private final Map<String, RequestConsumer> _topics;

    public TrainingServer() {
        _logger = Logger.getLogger("Server");
        
        _topics = Map.of(
            "trainer:getModel", this::requestModel,
            "trainer:submitData", this::processData
        );
    }

    public void initialize(ServerConfiguration configuration) throws IOException {
        assert(configuration.modelParametersSize > 2);

        _logger.info("Initializing...");

        SendableContext context = new SendableContext();
            context.registerSendable(ValueSendable.class);
            context.registerSendable(KeyValueSendable.class);
            context.registerSendable(StringSendable.class);

        _logger.config("Using configuration:\n" + configuration);

        _executor = new TrainerExecutor(configuration.trainerExecutableFile, configuration.trainerProgramFile);
        _trainerArguments = new TrainerExecutionArguments();
            _trainerArguments.outputFile = configuration.modelParametersFile;
            _trainerArguments.inputFile = configuration.dataStorageFile;
            _trainerArguments.degree = configuration.modelParametersSize - 1;

        _storage = new TrainingStorage(
            configuration.modelParametersFile,
            configuration.dataStorageFile
        );

        _parametersSize = configuration.modelParametersSize;
        _model = _storage.getModelParameters(_parametersSize);

        _socket = new ServerSocket(configuration.port);

        _logger.info("Waiting for connection");

        NetworkSocket connectionSocket = NetworkSocket.create(_socket.accept());
        _connection = new NetworkConnection(connectionSocket, context);

        _logger.info("Established connection.");
    }

    public void run() throws IOException {
        _logger.info("Running...");

        while (_connection.isConnected()) {
            NetworkServerRequest request = _connection.getRequest();
            
            if (request.getStatus() == NetworkStatus.STATUS_INTERRUPTED) {
                _logger.warning("Network request connection interrupted, stopping...");
                break;
            } else if (request.getPayload() == null) {
                _logger.warning("Got no payload on request, skipping...");
                continue;
            }

            KeyValueSendable payload = (KeyValueSendable) request.getPayload();
            _logger.info("Got request " + payload);

            StringSendable topic = (StringSendable) payload.getSendable("trainer.topic");
            RequestConsumer topicConsumer = _topics.get(topic.getValue());
            
            if (topicConsumer != null) {
                topicConsumer.process(request);
            } else {
                _logger.warning("Received request on unknown topic '" + topic.getValue());
                
                KeyValueSendable out = new KeyValueSendable();
                out.putSendable("reason", new StringSendable("Topic '" + topic.getValue() + "' does not exist."));
                request.fulfill(new NetworkServerResponse(NetworkStatus.STATUS_ERROR, out));
            }
        }
    }

    public void close() throws IOException {
        _connection.close();
        _socket.close();
    }

    // trainer:getModel
    private void requestModel(NetworkServerRequest request) throws IOException {
        NetworkStatus status;
        KeyValueSendable out = new KeyValueSendable();

        if (_model != null) {
            out.putInteger("trainer.model.size", _parametersSize);

            for (int i = 0; i < _parametersSize; i++) {
                out.putDouble("trainer.model.parameters["+i+"]", _model.get(i));
            }

            _logger.info("Fulfilled model request.");

            status = NetworkStatus.STATUS_OK;
        } else {
            _logger.warning("Received request for model information, when model was not loaded.");
            
            out.putSendable("reason", new StringSendable("Model does not exist."));
            status = NetworkStatus.STATUS_ERROR;
        }

        request.fulfill(new NetworkServerResponse(status, out));
    }
    
    // trainer:submitData
    private void processData(NetworkServerRequest request) throws IOException {
        KeyValueSendable payload = (KeyValueSendable) Objects.requireNonNull(request.getPayload());

        NetworkStatus status;
        KeyValueSendable out = new KeyValueSendable();

        try {
            TrainingData data = new TrainingData(
                payload.getDouble("trainer.data.speed"),
                payload.getDouble("trainer.data.distance")
            );

            _logger.info("Received values : " + data);

            _logger.info("Training model...");

            // append training data to storage
            _storage.writeTrainingData(data);
            // execute training
            _executor.train(_trainerArguments);
            _model = _storage.getModelParameters(_parametersSize);

            if (_model != null) {
                _logger.info("Finished training model. Got parameters:\n" + _model);

                out.putInteger("trainer.model.size", _parametersSize);
                for (int i = 0; i < _parametersSize; i++) {
                    out.putDouble("trainer.model.parameters[" + i + "]", _model.get(i));
                }

                _logger.info("Completed data submission.");

                status = NetworkStatus.STATUS_OK;
            } else {
                _logger.warning("Finished training model. Got no parameters.");
                out.putSendable("reason", new StringSendable("Model does not exist."));
                status = NetworkStatus.STATUS_UNAVAILABLE;
            }
        } catch (Exception e) {
            _logger.log(Level.SEVERE,"Exception encountered during data processing", e);
            out.putSendable("reason", new StringSendable("Encountered unexpected exception."));
            status = NetworkStatus.STATUS_ERROR;
        }

        request.fulfill(new NetworkServerResponse(status, out));
    }
}
