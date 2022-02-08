package frc.robot.training;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.jetbrains.annotations.Nullable;

public class TrainingStorage {
    private final StorageFile _modelStorage;
    private final StorageFile _dataStorage;

    public TrainingStorage(StorageFile modelStorage, StorageFile dataStorage) throws IOException {
        _dataStorage = dataStorage;
        _modelStorage = modelStorage;
    }

    public void writeTrainingData(TrainingData data) throws IOException {
        CSVWriter writer = new CSVWriter(_dataStorage.getWriter());

        writer.writeNext(
            new String[]{ Double.toString(data.distance), Double.toString(data.speed) }
        );

        writer.close();
    }

    @Nullable
    public ModelParameters getModelParameters(int size) throws IOException {
        FileReader fileReader = _modelStorage.getReader();
            fileReader.reset();

        CSVReader reader = new CSVReader(fileReader);
        String[] rawparams = reader.readNext();
        reader.close();
        
        if (rawparams.length == 0) {
            return null;
        } else if (rawparams.length != size) {
            throw new IOException("Expected " + size + " parameters, got " + rawparams.length);
        }

        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = Double.parseDouble(rawparams[i]);
        }

        return new ModelParameters(values);
    }
}
