package org.frc.team5409.robot.training.protocol;

import org.frc.team5409.robot.training.util.Type;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class ValueSendable implements NetworkSendable {
    @SuppressWarnings("unused")
    private static void register(SendableRegistar registry) {
        registry.registerFactory(ValueSendable.WHAT, ValueSendable.class, ValueSendable::new);
    }

    public static final long WHAT = -7383229974325473331L;

    public static final byte VALUE_TYPE_BYTE    = 0x0;
    public static final byte VALUE_TYPE_CHAR    = 0x1;
    public static final byte VALUE_TYPE_SHORT   = 0x2;
    public static final byte VALUE_TYPE_INT     = 0x3;
    public static final byte VALUE_TYPE_FLOAT   = 0x4;
    public static final byte VALUE_TYPE_LONG    = 0x5;
    public static final byte VALUE_TYPE_DOUBLE  = 0x6;
    public static final byte VALUE_TYPE_BOOLEAN = 0x7;

    protected Object   _value;
    protected Class<?> _type;

    public ValueSendable() {
        _value = null;
        _type  = Object.class;
    }

    public ValueSendable(ValueSendable copy) {
        _value = copy._value;
        _type = copy._type;
    }

    public ValueSendable(Object value) {
        Class<?> type = value.getClass();

        if (!Type.isPrimitiveType(type))
            throw new IllegalArgumentException("Cannot apply non primitive type '" + type.getSimpleName() + "'");

        if (Type.isUnwrappedType(type))
            type = Type.getUnwrappedType(type);

        _value = value;
        _type = type;
    }

    @Override
    public long what() {
        return WHAT;
    }

    @Override
    public void read(SendableContext context, DataInputStream stream) throws IOException {
        byte valueType = stream.readByte();

        switch(valueType) {
            case VALUE_TYPE_BYTE:
                _value = stream.readByte();
                _type = Byte.class;
                break;
            case VALUE_TYPE_CHAR:
                _value = stream.readChar();
                _type = Character.class;
                break;
            case VALUE_TYPE_SHORT:
                _value = stream.readShort();
                _type = Short.class;
                break;
            case VALUE_TYPE_INT:
                _value = stream.readInt();
                _type = Integer.class;
                break;
            case VALUE_TYPE_FLOAT:
                _value = stream.readFloat();
                _type = Float.class;
                break;
            case VALUE_TYPE_LONG:
                _value = stream.readLong();
                _type = Long.class;
                break;
            case VALUE_TYPE_DOUBLE:
                _value = stream.readDouble();
                _type = Double.class;
                break;
            case VALUE_TYPE_BOOLEAN:
                _value = stream.readBoolean();
                _type = Boolean.class;
                break;
            default:
                throw new IOException("Unexpected ValueSendable type 0x"+Integer.toHexString(valueType));
        }
    }

    @Override
    public void write(SendableContext context, DataOutputStream stream) throws IOException {
        if (_value == null)
            throw new IOException("Cannot write 'null' value.");

        if (_type.equals(Byte.class)) {
            stream.writeByte((Byte) _value);
        } else if (_type.equals(Character.class)) {
            stream.writeChar((Character) _value);
        } else if (_type.equals(Short.class)) {
            stream.writeShort((Short) _value);
        } else if (_type.equals(Integer.class)) {
            stream.writeInt((Integer) _value);
        } else if (_type.equals(Long.class)) {
            stream.writeLong((Long) _value);
        } else if (_type.equals(Boolean.class)) {
            stream.writeBoolean((Boolean) _value);
        } else {
            throw new IOException("Unexpected ValueSendable class");
        }
    }

    public void setValue(Object value) {
        Class<?> type = value.getClass();

        if (!Type.isPrimitiveType(type))
            throw new IllegalArgumentException("Cannot apply non primitive type '" + type.getSimpleName() + "'");

        if (Type.isUnwrappedType(type))
            type = Type.getUnwrappedType(type);

        _value = value;
        _type = type;
    }

    @Nullable
    public Object getValue() {
        return _value;
    }

    @Nullable
    public <T> T getValue(Class<T> type) {
        if (!type.isAssignableFrom(_type))
            throw new IllegalArgumentException("Cannot cast value type '" + _type.getSimpleName() + "' to type '" + _type.getSimpleName() + "'");
        return Type.uncheckedCast(_value);
    }

    @Nullable
    public Class<?> getType() {
        return _type;
    }
}