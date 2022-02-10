package frc.robot.training;

public class TrainingData {
    public final double speed;
    public final double distance;

    public TrainingData(double speed, double distance) {
        this.speed = speed;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "{\n" +
            "\tspeed    = " + speed +
            "\n\tdistance = " + distance +
            "\n}";
    }
}
