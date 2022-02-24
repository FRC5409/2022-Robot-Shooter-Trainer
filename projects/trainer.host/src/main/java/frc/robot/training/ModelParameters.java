package frc.robot.training;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModelParameters {
    private List<Double> _values;

    public ModelParameters() {
        _values = Collections.emptyList();
    }

    public ModelParameters(double[] values) {
        _values = Arrays.stream(values)
            .boxed()
            .collect(Collectors.toUnmodifiableList());
    }

    public double get(int index) throws IndexOutOfBoundsException {
        return _values.get(index);
    } 

    public List<Double> values() {
        return _values;
    }

    public int size() {
        return _values.size();
    }

    @Override
    public String toString() {
        return _values.toString();
    }
}
