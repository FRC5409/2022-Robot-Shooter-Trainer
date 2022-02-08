package frc.robot.training.protocol;

import frc.robot.training.util.Factory;

final class SendableRegistryEntry<T extends NetworkSendable> {
    public final Class<T> type; 
    public final Factory<T> factory;

    public SendableRegistryEntry(Class<T> type, Factory<T> factory) {
        this.type = type;
        this.factory = factory;
    }
}