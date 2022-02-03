package org.frc.team5409.robot.training.protocol;

import org.frc.team5409.robot.training.util.Factory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SendableContext {
    private final Map<Long, Factory<? extends NetworkSendable>> _factories;

    public SendableContext() {
        _factories = new HashMap<>();
    }

    public void registerSendableFactory(long what, Factory<? extends NetworkSendable> factory) {
        if (_factories.containsKey(what))
            throw new IllegalArgumentException("Conflict at 'what' 0x"+Long.toHexString(what));
        _factories.put(what, factory);
    }

    @Nullable
    public Factory<? extends NetworkSendable> getSendableFactory(long what) {
        return _factories.get(what);
    }

}
