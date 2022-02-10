package frc.robot.training;

import java.util.Arrays;

public class ModelParameters {
    private double[] _values;

    public ModelParameters() {
        _values = new double[0];
    }

    public ModelParameters(double[] values) {
        _values = new double[values.length];
        System.arraycopy(values, 0, _values, 0, values.length);
    }

    public double get(int i) throws IndexOutOfBoundsException {
        if (i > _values.length || i < 0)
            throw new IndexOutOfBoundsException(i);
    
        return _values[i];
    } 

    public int size() {
        return _values.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(_values);
    }
}
