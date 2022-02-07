package frc.robot.training.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import frc.robot.training.protocol.SendableContext;

public interface NetworkSendable {
    long what();
    void read(SendableContext context, DataInputStream stream) throws IOException;
    void write(SendableContext context, DataOutputStream stream) throws IOException;
}
