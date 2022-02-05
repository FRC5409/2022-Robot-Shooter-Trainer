package org.frc.team5409.robot.training.protocol;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.frc.team5409.robot.training.util.Factory;
import org.jetbrains.annotations.Nullable;

public class SendableRegistry {
    private final Map<Long, SendableRegistryEntry<?>> _entries;

    public SendableRegistry() {
        _entries = new HashMap<>();
    }

    public void registerSendable(Class<? extends NetworkSendable> type) {
        SendableRegistar registar = new SendableRegistar(this);
        configureSendableRegistration(type, registar);
    }

    @Nullable
    public Factory<? extends NetworkSendable> getSendableFactory(long what) {
        SendableRegistryEntry<?> entry = _entries.get(what);
        if (entry == null)
            return null;
            return entry.factory;
    }

    @Nullable
    public Class<? extends NetworkSendable> getSendableType(long what) {
        SendableRegistryEntry<?> entry = _entries.get(what);
        if (entry == null)
            return null;
            return entry.type;
    }

    <T extends NetworkSendable>
    void registerSendableFactory(long what, Class<T> type, Factory<T> factory) {
        if (_entries.containsKey(what)) {
            SendableRegistryEntry<?> conflictedEntry = _entries.get(what);

            if (!conflictedEntry.type.equals(type)) {
                throw new IllegalArgumentException("Conflicting 'what' of 0x" 
                    + Long.toHexString(what) + " between '" + conflictedEntry.type.getSimpleName()
                        + "' and " + type.getSimpleName() + "'");
            }
        } else {
            _entries.put(what, new SendableRegistryEntry<>(type, factory));
        }
    }


    private void configureSendableRegistration(Class<? extends NetworkSendable> type, SendableRegistar registar) {
        try {
            Method registerCallback = type.getMethod("register", SendableRegistar.class);
            registerCallback.invoke(registar);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class '" + type.getSimpleName()  + "' does not define \n static register(SendableRegistar registar)");
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to register Sendable of type '" + type.getSimpleName()  + "'", e);
        }
    }
}