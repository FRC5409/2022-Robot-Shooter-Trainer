package frc.robot.training;

import java.io.IOException;

public class Main {
    public static int main(String[] args) {
        try {
            ServerConfiguration config = new ServerConfiguration();
                config.port = 5409;
                config.dataStorageFile = "data.csv";
                config.modelParametersFile = "model.csv";

            // spawn new server
            TrainingServer server = new TrainingServer();
            server.initialize(config);

            // run server
            server.run();

            // close server
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }   
}
