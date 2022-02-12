package frc.robot.training;

import java.io.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.jetbrains.annotations.Nullable;

public class TrainingStorage {
    private final File _modelStorage;
    private final File _dataStorage;

    public TrainingStorage(File modelStorage, File dataStorage) throws IOException {
        if (!modelStorage.exists() && !modelStorage.createNewFile())
            throw new IOException("Failed to create file '" + modelStorage + "'");

        if (!dataStorage.exists() && !dataStorage.createNewFile())
            throw new IOException("Failed to create file '" + dataStorage + "'");

        _dataStorage = dataStorage;
        _modelStorage = modelStorage;
    }

    public void writeTrainingData(TrainingData data) throws IOException {
        FileWriter fileWriter = new FileWriter(_dataStorage, true);
        CSVWriter writer = new CSVWriter(fileWriter, ',', ' ', '"', "\n");

        writer.writeNext(
            new String[]{
                Double.toString(data.distance), Double.toString(data.speed)
            }
        );

        writer.close();
        fileWriter.close();
    }

    @Nullable
    public ModelParameters getModelParameters(int size) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(_modelStorage));
        String[] params = reader.readNext();
        reader.close();
        
        if (params == null || params.length == 0) {
            return null;
        } else if (params.length != size) {
            throw new IOException("Expected " + size + " parameters, got " + params.length);
        }

        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            // if any of the params are nan values, discard model
            if (params[i].equals("nan")) return null;
            values[i] = Double.parseDouble(params[i]);
        }

        return new ModelParameters(values);
    }
}
