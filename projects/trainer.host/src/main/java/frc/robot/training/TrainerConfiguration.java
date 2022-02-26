package frc.robot.training;

import java.io.IOException;

import org.jetbrains.annotations.Nullable;

public class TrainerConfiguration {
    private final String _name;
    private final int _parametersSize;
    private final TrainingStorage _storage;
    private final TrainerExecutionArguments _trainerArguments;

    @Nullable
    private ModelParameters _model;

    public TrainerConfiguration(
        String name,
        int parametersSize,
        TrainingStorage storage,
        TrainerExecutionArguments trainerArguments
    ) {
        _name             = name;
        _trainerArguments = trainerArguments;
        _storage          = storage;
        _parametersSize   = parametersSize;
        _model            = null;
    }

    public void submitData(TrainingData data) throws IOException {
        _storage.writeTrainingData(data);
    }

    public void executeTraining(TrainerExecutor executor) throws IOException {
        executor.train(_trainerArguments);
        _model = _storage.getModelParameters(_parametersSize);
    }

    public ModelParameters getModel() {
        return _model;
    }

    public int getParametersSize() {
        return _parametersSize;
    }

    public String getName() {
        return _name;
    }
}
