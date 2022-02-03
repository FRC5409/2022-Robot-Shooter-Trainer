package org.frc.team5409.robot.training.protocol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StringSendable implements NetworkSendable {
    public static final long WHAT = 3273615128644272490L;
    private String _value;

    public StringSendable() {
        _value = null;
    }

    public StringSendable(String value) {
        _value = value;
    }

    public StringSendable(StringSendable copy) {
        _value = copy._value;
    }

    @Override
    public long what() {
        return WHAT;
    }

    @Override
    public void read(SendableContext context, DataInputStream stream) throws IOException {
        StringBuilder buffer = new StringBuilder();

        char i;
        while ((i = stream.readChar()) != '\0') {
            buffer.append(i);
        }

        _value = buffer.toString();
    }

    @Override
    public void write(SendableContext context, DataOutputStream stream) throws IOException {
        if (_value == null)
            throw new IOException("Cannot write 'null' value.");

        // TODO see if this includes EOF character
        stream.write(_value.getBytes(StandardCharsets.US_ASCII));
    }

    public void setValue(@NotNull String value) {
        _value = value;
    }

    @Nullable
    public String getValue() {
        return _value;
    }
}
