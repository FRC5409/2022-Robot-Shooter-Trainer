package frc.robot.training;

public class ModelParameters {
    private double[] _values;

    public ModelParameters() {
        _values = new double[0];
    }

    public ModelParameters(double[] values) {
        _values = new double[values.length];
        System.arraycopy(_values, 0, values, 0, values.length);
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
        return _values.toString();
    }
}
