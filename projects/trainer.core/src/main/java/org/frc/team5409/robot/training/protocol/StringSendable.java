package org.frc.team5409.robot.training.protocol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StringSendable implements NetworkSendable {
    @SuppressWarnings("unused")
    private static void register(SendableRegistar registry) {
        registry.registerFactory(StringSendable.WHAT, StringSendable.class, StringSendable::new);
    }

    public static final long WHAT = 3273615128644272490L;

    private static final byte EOF = '\0';

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
        StringBuilder builder = new StringBuilder();

        byte[] buffer = new byte[1024];

        int n = 0;
        byte i;

        while ((i = stream.readByte()) != EOF) {
            if (n == 1024) {
                builder.append(new String(buffer, 0, n+1, StandardCharsets.US_ASCII));
                n = 0;
            }

            buffer[n] = i;
            n++;
        }
        
        if (n != 0) {
            builder.append(new String(buffer, 0, n+1, StandardCharsets.US_ASCII));
        }

        _value = builder.toString();
    }

    @Override
    public void write(SendableContext context, DataOutputStream stream) throws IOException {
        if (_value == null)
            throw new IOException("Cannot write 'null' value.");

        // TODO see if this includes EOF character
        stream.write(_value.getBytes(StandardCharsets.US_ASCII));
        stream.writeByte(EOF);
    }

    public void setValue(@NotNull String value) {
        _value = value;
    }

    @Nullable
    public String getValue() {
        return _value;
    }
}
