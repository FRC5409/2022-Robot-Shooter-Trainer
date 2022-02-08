package frc.robot.training.protocol;

import frc.robot.training.util.Factory;

public class SendableRegistryBuilder {
    private final SendableRegistry _registry;

    SendableRegistryBuilder(SendableRegistry registry) {
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
