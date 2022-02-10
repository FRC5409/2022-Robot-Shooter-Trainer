package frc.robot.training;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrainerExecutor {
    private final File _executable;
    private final File _program;
    private final Logger _logger;
    
    public TrainerExecutor(File executable, File program) {
        _executable = executable;
        _program = program;
        _logger = Logger.getLogger("Executor");
    }

    public void train(TrainerExecutionArguments arguments) {
        try {
            _logger.info("Executing training with arguments : " + arguments);

            // build command
            String command = _executable.getPath() +
                ' ' + _program.getAbsolutePath() +
                ' ' + arguments.parse();

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            Logger processLogger = Logger.getLogger("TrainerProcess");
            processLogger.setParent(_logger);

            try {
                String nextLine;
                while ((nextLine = processOutputReader.readLine()) != null)
                    processLogger.info(nextLine);

            } catch (EOFException e) {
                // suppress error
            } finally {
                process.waitFor();
                processOutputReader.close();
            }
        } catch (Exception e) {
            _logger.log(Level.SEVERE, "Exception encountered during training process", e);
        }
    }
}
