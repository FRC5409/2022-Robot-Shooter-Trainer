package frc.robot.training;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TrainerEnvironment {
    private static final String MODEL_CONFIGURATION_NAME = "model.csv";
    private static final String DATA_CONFIFURATION_NAME  = "data.csv";

    private final Map<String, TrainerConfiguration> _configurations;
    private final File   _directory;
    private final Logger _logger;
    private final int    _parametersSize;
    
    public TrainerEnvironment(int parametersSize, File directory, Logger parent) throws IOException {
        if (!directory.exists()) {
            if (!directory.mkdirs())
                throw new IOException("Failed to create training environment");
        } else if (!directory.isDirectory())
            throw new IllegalArgumentException("Expected directory for trainer environment, got file.");

        _logger = Logger.getLogger("TrainerEnvironment");
        _logger.setParent(parent);

        _directory = directory;
        _parametersSize = parametersSize;
        _configurations = new HashMap<>();
    }

    public TrainerConfiguration getConfiguration(String name) throws IOException {
        if (_configurations.containsKey(name))
            return _configurations.get(name);
        else {
            TrainerConfiguration loadedConfiguration;

            File configurationFile = new File(_directory, name);
            if (configurationFile.exists() && configurationFile.isDirectory()) {
                loadedConfiguration = loadConfiguration(name, configurationFile);
                _logger.info("Loaded configuration '"+name+"' from file");
            } else {
                loadedConfiguration = createConfiguration(name, configurationFile);
                _logger.info("Created configuration '"+name+"'");
            }

            _configurations.put(name, loadedConfiguration);
            return loadedConfiguration;
        }
    }

    private TrainerConfiguration createConfiguration(String name, File directory) throws IOException {
        if (!directory.mkdirs())
            throw new IOException("Failed to create configuration directory");
        
        File model = new File(directory, MODEL_CONFIGURATION_NAME);
        if (!model.createNewFile())
            throw new IOException("Failed to create model file");
            
        File data = new File(directory, DATA_CONFIFURATION_NAME);
        if (!data.createNewFile())
            throw new IOException("Failed to create data file");

        TrainerExecutionArguments executionArguments = new TrainerExecutionArguments();
            executionArguments.inputFile = data;
            executionArguments.outputFile = model;
            executionArguments.degree = _parametersSize - 1;
        
        TrainingStorage storage = new TrainingStorage(model, data);
        return new TrainerConfiguration(name, _parametersSize, storage, executionArguments, storage.getModelParameters(_parametersSize));
    }

    private TrainerConfiguration loadConfiguration(String name, File directory) throws IOException {
        File model = new File(directory, MODEL_CONFIGURATION_NAME);
        if (!model.exists())
            throw new IOException("Model file does not exist.");
            
        File data = new File(directory, DATA_CONFIFURATION_NAME);
        if (!data.exists())
            throw new IOException("Data file does not exist.");

        TrainerExecutionArguments executionArguments = new TrainerExecutionArguments();
            executionArguments.inputFile = data;
            executionArguments.outputFile = model;
            executionArguments.degree = _parametersSize - 1;
        
        TrainingStorage storage = new TrainingStorage(model, data);
        return new TrainerConfiguration(name, _parametersSize, storage, executionArguments, storage.getModelParameters(_parametersSize));
    }
}
