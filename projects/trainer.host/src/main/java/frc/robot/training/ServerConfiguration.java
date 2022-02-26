package frc.robot.training;

import java.io.File;

public class ServerConfiguration {
    public Integer port;
    public Integer modelParametersSize;
    public File trainerEnvironment;
    public File trainerExecutableFile;
    public File trainerProgramFile;


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{ \n");

        if (port != null) {
            builder.append("\tport                  = ");
            builder.append(port);
        }

        if (modelParametersSize != null) {
            builder.append("\n\tmodelParametersSize   = ");
            builder.append(modelParametersSize);
        }

        if (trainerEnvironment != null) {
            builder.append("\n\ttrainerEnvironment    = ");
            builder.append(trainerEnvironment.getAbsolutePath());
        }

        if (trainerProgramFile != null) {
            builder.append("\n\ttrainerProgramFile    = ");
            builder.append(trainerProgramFile.getAbsolutePath());
        }

        if (trainerExecutableFile != null) {
            builder.append("\n\ttrainerExecutableFile = ");
            builder.append(trainerExecutableFile);
        }

        if (builder.length() > 3)
            builder.append('\n');

        builder.append('}');


        return builder.toString();
    }
}
