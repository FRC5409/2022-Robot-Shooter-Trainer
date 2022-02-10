package frc.robot.training;

import java.io.File;

public class ServerConfiguration {
    public Integer port;
    public Integer modelParametersSize;
    public File dataStorageFile;
    public File modelParametersFile;
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
        if (dataStorageFile != null) {
            builder.append("\n\tdataStorageFile       = ");
            builder.append(dataStorageFile);
        }
        if (modelParametersFile != null) {
            builder.append("\n\tmodelParametersFile   = ");
            builder.append(modelParametersFile);
        }
        if (trainerProgramFile != null) {
            builder.append("\n\ttrainerProgramFile    = ");
            builder.append(trainerProgramFile);
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
