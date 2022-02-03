package org.frc.team5409.robot.training.protocol;

import org.frc.team5409.robot.training.collections.Entry;
import org.frc.team5409.robot.training.collections.EntryIterable;
import org.frc.team5409.robot.training.collections.EntryIterator;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Inspired by android bundles
public class KeyValueSendable implements NetworkSendable, EntryIterable<String, Object> {
    public static final long WHAT = 4019083808795941718L;

    private static final byte STREAM_COLLECTION_ITEM = 0x1;
    private static final byte STREAM_COLLECTION_END = 0x2;


    protected Map<String, Object> _values;

    public KeyValueSendable() {
        _values = new HashMap<>();
    }

    public KeyValueSendable(KeyValueSendable copy) {
        _values = new HashMap<>(copy._values);
    }

    public void putBoolean(@NotNull String name, boolean value) {
        putValue(name, value);
    }

    public void putByte(@NotNull String name, byte value) {
        putValue(name, value);
    }

    public void putCharacter(@NotNull String name, char value) {
        putValue(name, value);
    }

    public void putDouble(@NotNull String name, double value) {
        putValue(name, value);
    }

    public void putFloat(@NotNull String name, float value) {
        putValue(name, value);
    }

    public void putInteger(@NotNull String name, int value) {
        putValue(name, value);
    }

    public void putLong(@NotNull String name, long value) {
        putValue(name, value);
    }

    public void putShort(@NotNull String name, short value) {
        putValue(name, value);
    }

    public void putString(@NotNull String name, String value) {
        putValue(name, value);
    }

    public boolean getBoolean(@NotNull String name) {
        return getValue(name, Boolean.class);
    }

    public byte getByte(@NotNull String name) {
        return getValue(name, Byte.class);
    }

    public char getCharacter(@NotNull String name) {
        return getValue(name, Character.class);
    }

    public double getDouble(@NotNull String name) {
        return getValue(name, Double.class);
    }

    public float getFloat(@NotNull String name) {
        return getValue(name, Float.class);
    }

    public int getInteger(@NotNull String name) {
        return getValue(name, Integer.class);
    }

    public long getLong(@NotNull String name) {
        return getValue(name, Long.class);
    }

    public short getShort(@NotNull String name) {
        return getValue(name, Short.class);
    }

    public String getString(@NotNull String name) {
        return getValue(name, String.class);
    }

    public void clear() {
        _values.clear();
    }

    public boolean exists(@NotNull String name) {
        return _values.containsKey(name);
    }

    protected void putValue(String name, Object value) {
        _values.put(name, value);
    }

    protected <T> T getValue(String name, Class<T> clazz) throws ClassCastException {
        if (!_values.containsKey(name))
            return null;

        Object value = _values.get(name);
        if (!clazz.isAssignableFrom(value.getClass()))
            throw new ClassCastException("Key with name '" + name + "' and class '" +
                value.getClass().getSimpleName() + "' cannot be casted to '" + clazz.getSimpleName() + "'");

        return clazz.cast(value);
    }

    @NotNull
    @Override
    public EntryIterator<String, Object> iterator() {
        return new EntryIterator<>(_values);
    }

    @Override
    public long what() {
        return WHAT;
    }

    @Override
    public void read(SendableContext context, DataInputStream stream) throws IOException {
        SendableReader reader = new SendableReader(context, stream);
        _values.clear();

        byte status;
        while ((status = stream.readByte()) != STREAM_COLLECTION_END) {
            StringSendable key = (StringSendable) reader.read(StringSendable.WHAT);
            ValueSendable value = (ValueSendable) reader.read(ValueSendable.WHAT);

            _values.put(key.getValue(), value.getValue());
        }
    }

    @Override
    public void write(SendableContext context, DataOutputStream stream) throws IOException {
        SendableWriter writer = new SendableWriter(context, stream);

        StringSendable key = new StringSendable();

        for (Entry<String, Object> entry : this) {
            stream.writeByte(STREAM_COLLECTION_ITEM);

            key.setValue(entry.key());
            writer.write(key);

            Object value = entry.value();
            if (value instanceof String) {
                key.setValue((String) value);
                writer.write(key);
            } else {
                writer.write(new ValueSendable(value));
            }

            stream.writeByte(STREAM_COLLECTION_ITEM);
        }
        stream.writeByte(STREAM_COLLECTION_END);
    }
}