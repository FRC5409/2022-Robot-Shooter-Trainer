package frc.robot.training;

import java.io.IOException;

public class Main {
    public static final int DEFAULT_PORT = 5409;

    public static int main(String[] args) {
        try {
            assert (args.length >= 4);
            ServerConfiguration config = new ServerConfiguration();
            if (args.length == 4) {
                config.port = DEFAULT_PORT;
                config.dataStorageFile = args[0];
                config.modelParametersFile = args[1];
                config.modelParametersSize = Integer.parseInt(args[2]);
                config.trainerExecutableFile = args[3];
            } else {
                config.port = Integer.parseInt(args[0]);
                config.dataStorageFile = args[1];
                config.modelParametersFile = args[2];
                config.modelParametersSize = Integer.parseInt(args[3]);
                config.trainerExecutableFile = args[4];
            }

            // spawn new server
            TrainingServer server = new TrainingServer();
            server.initialize(config);

            // run server
            server.run();

            // close server when finished
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }   
}
