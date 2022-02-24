package frc.robot.training.protocol.generic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import frc.robot.training.protocol.NetworkSendable;
import frc.robot.training.protocol.SendableContext;
import frc.robot.training.protocol.SendableReader;
import frc.robot.training.protocol.SendableRegistryBuilder;
import frc.robot.training.protocol.SendableWriter;

public class ArraySendable implements NetworkSendable, Iterable<NetworkSendable> {
    public static final long WHAT = -197263571125403821L;

    @SuppressWarnings("unused")
    private static void register(SendableRegistryBuilder registry) {
        registry.registerFactory(WHAT, ArraySendable.class, ArraySendable::new);
    }

    private List<NetworkSendable> _values;

    public ArraySendable() {
        _values = new ArrayList<>(0);
    }

    public ArraySendable(Collection<NetworkSendable> values) {
        _values = new ArrayList<>(values);
    }

    public void add(NetworkSendable value) {
        _values.add(value);
    }

    public NetworkSendable remove(int index) {
        return _values.remove(index);
    }

    public void add(int index, NetworkSendable value) {
        _values.add(index, value);
    }

    public void addAll(Collection<NetworkSendable> values) {
        _values.addAll(values);
    }

    public void addAll(int index, Collection<NetworkSendable> values) {
        _values.addAll(index, values);
    }

    public NetworkSendable get(int index) throws IndexOutOfBoundsException {
        return _values.get(index);
    }

    public <T extends NetworkSendable>
    T get(int index, Class<T> type) throws IndexOutOfBoundsException, ClassCastException {
        return type.cast(_values.get(index));
    }

    public void clear() {
        _values.clear();
    }
    
    public int size() {
        return _values.size();
    }

    @Override
    public Iterator<NetworkSendable> iterator() {
        return _values.iterator();
    }

    @Override
    public long what() {
        return WHAT;
    }

    @Override
    public void read(SendableContext context, DataInputStream stream) throws IOException {
        SendableReader reader = new SendableReader(context, stream);
        _values.clear();

        int size = stream.readInt();
        for (int i = 0; i < size; i++) {
            _values.add(reader.read());
        }
    }

    @Override
    public void write(SendableContext context, DataOutputStream stream) throws IOException {
        SendableWriter writer = new SendableWriter(context, stream);
        
        stream.writeInt(_values.size());
        for (NetworkSendable value : _values) {
            writer.write(value);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ArraySendable[" + size() + "] {\n");

        if (size() > 0) {
            for (NetworkSendable value : _values) {
                builder.append("\t\"");
                builder.append(value);
                builder.append(",\n");
            }

            builder.setCharAt(builder.length() - 2, ' ');
        }

        builder.append("}");

        return builder.toString();
    }
}
