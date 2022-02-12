package frc.robot;

import frc.robot.training.Main;

public class TestServer {
    public static void main(String[] args) {
        Main.main(new String[]{
            "5409",
            ".temp/training/data.csv",
            ".temp/training/model.csv",
            "4",
            "python.exe",
            "python-projects/trainer/main.py"
        });
    }
}
