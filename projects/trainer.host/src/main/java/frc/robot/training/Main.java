package frc.robot.training;

import java.io.File;
import java.io.IOException;

public class Main {
    public static final int DEFAULT_PORT = 5409;

    public static void main(String[] args) {
        ServerConfiguration config = parseArguments(args);
        boolean active = true;

        try {
            while (active) {
                // spawn new server
                TrainingServer server = new TrainingServer();
                server.initialize(config);

                try {
                    // run server
                    server.run();
                    active = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (active)
                    System.out.println("Restarting server...");
                else
                    System.out.println("Closing server...");
                // close server when finished
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server closed.");
    }   

    private static ServerConfiguration parseArguments(String[] args) {
        assert (args.length >= 5);
        ServerConfiguration config = new ServerConfiguration();
        if (args.length == 5) {
            config.port = DEFAULT_PORT;
            config.dataStorageFile = new File(args[0]);
            config.modelParametersFile = new File(args[1]);
            config.modelParametersSize = Integer.parseInt(args[2]);
            config.trainerExecutableFile = new File(args[3]);
            config.trainerProgramFile = new File(args[4]);
        } else {
            config.port = Integer.parseInt(args[0]);
            config.dataStorageFile = new File(args[1]);
            config.modelParametersFile = new File(args[2]);
            config.modelParametersSize = Integer.parseInt(args[3]);
            config.trainerExecutableFile = new File(args[4]);
            config.trainerProgramFile = new File(args[5]);
        }

        return config;
    }
}
