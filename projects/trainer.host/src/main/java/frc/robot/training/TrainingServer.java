package frc.robot.training;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import frc.robot.training.protocol.*;
import frc.robot.training.protocol.generic.ArraySendable;
import frc.robot.training.protocol.generic.BundleSendable;
import frc.robot.training.protocol.generic.StringSendable;
import frc.robot.training.protocol.generic.ValueSendable;

public class TrainingServer {
    private final Logger       _logger;
    private final Map<String, RequestConsumer> _topics;

    private ServerSocket       _socket;
    private NetworkConnection  _connection;

    private TrainerExecutor    _executor;
    private TrainerEnvironment _environment;
    private int                _parametersSize;

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
            context.registerSendable(BundleSendable.class);
            context.registerSendable(StringSendable.class);
            context.registerSendable(ArraySendable.class);

        _logger.info("Using configuration:\n" + configuration);

        _executor = new TrainerExecutor(configuration.trainerExecutableFile, configuration.trainerProgramFile);
        _environment = new TrainerEnvironment(configuration.modelParametersSize, configuration.trainerEnvironment, _logger);
        _parametersSize = configuration.modelParametersSize;

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

            BundleSendable payload = (BundleSendable) request.getPayload();
            _logger.info("Got request " + payload);

            StringSendable topic = (StringSendable) payload.getSendable("trainer.topic");
            RequestConsumer topicConsumer = _topics.get(topic.getValue());
            
            if (topicConsumer != null) {
                topicConsumer.process(request);
            } else {
                _logger.warning("Received request on unknown topic '" + topic.getValue());
                
                BundleSendable out = new BundleSendable();
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
        BundleSendable payload = (BundleSendable) request.getPayload();

        StringSendable configurationName = (StringSendable) payload.getSendable("trainer.configuration");
        TrainerConfiguration configuration = _environment.getConfiguration(configurationName.getValue());

        NetworkStatus status;
        BundleSendable out = new BundleSendable();

        ModelParameters model = configuration.getModel();
        if (model != null) {
            out.putSendable(
                "trainer.model.parameters",
                new ArraySendable(
                    model.values().stream()
                        .map(ValueSendable::new)
                        .collect(Collectors.toUnmodifiableList())
                )
            );

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
        BundleSendable payload = (BundleSendable) Objects.requireNonNull(request.getPayload());

        NetworkStatus status;
        BundleSendable out = new BundleSendable();

        try {
            StringSendable configurationName = (StringSendable) payload.getSendable("trainer.configuration");
            TrainerConfiguration configuration = _environment.getConfiguration(configurationName.getValue());

            TrainingData data = new TrainingData(
                payload.getDouble("trainer.data.speed"),
                payload.getDouble("trainer.data.distance")
            );

            _logger.info("Training model on configuration '"+configuration.getName()+"'");

            // append training data to storage
            configuration.submitData(data);
            
            // execute training
            configuration.executeTraining(_executor);

            ModelParameters model = configuration.getModel();
            if (model != null) {
                _logger.info("Finished training model. Got parameters:\n" + model);
                
                out.putSendable("trainer.configuration", configurationName);
                out.putSendable(
                    "trainer.model.parameters",
                    new ArraySendable(
                        model.values().stream()
                            .map(ValueSendable::new)
                            .collect(Collectors.toUnmodifiableList())
                    )
                );

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
