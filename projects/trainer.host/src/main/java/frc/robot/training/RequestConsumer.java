package frc.robot.training;

import java.io.IOException;

import frc.robot.training.protocol.NetworkServerRequest;

public interface RequestConsumer {
    void process(NetworkServerRequest request) throws IOException;
}
