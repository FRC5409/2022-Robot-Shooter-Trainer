package frc.robot.training;

import java.io.File;

public class TrainerExecutor {
    private final File _executable;
    
    public TrainerExecutor(File executable) {
        _executable = executable;
    }

    public void train() {
        try {
            Process process = Runtime.getRuntime()
                .exec(_executable.getAbsolutePath());
                
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
