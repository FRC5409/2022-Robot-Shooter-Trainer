package org.frc.team5409.robot.training.protocol;

import org.frc.team5409.robot.training.util.Factory;

public class SendableRegistar {
    private final SendableRegistry _registry;

    SendableRegistar(SendableRegistry registry) {
        _registry = registry;
    }

    public <T extends NetworkSendable>
    void registerFactory(long what, Class<T> type, Factory<T> factory) {
        _registry.registerSendableFactory(what, type, factory);
    }

    public void registerSendable(Class<? extends NetworkSendable> type) {
        _registry.registerSendable(type);
    }
}
