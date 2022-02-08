package frc.robot.training;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StorageFile {
    private final File _target;

    private FileReader _reader;
    private FileWriter _writer;


    public StorageFile(String target) throws IOException {
        this(new File(target));
    }

    public StorageFile(File target) throws IOException {
        if (!target.exists())
            throw new FileNotFoundException("File '" + target.getCanonicalPath() + "' does not exist.");
        
        _target = target;
        _reader = null;
        _writer = null;
    }

    public FileReader getReader() throws IOException {
        if (_reader == null)
            _reader = new FileReader(_target);
        return _reader;
    }

    public FileWriter getWriter() throws IOException {
        if (_writer == null)
            _writer = new FileWriter(_target);
        return _writer;
    }



    public void close() throws IOException {
        if (_reader != null)
            _reader.close();

        if (_writer != null)
            _writer.close();
    }
}
