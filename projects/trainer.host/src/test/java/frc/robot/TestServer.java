package frc.robot;

import frc.robot.training.Main;

public class TestServer {
    
    public static void main(String[] args) {
        Main.main(new String[]{
            "5409",
            "curve-fit\\in.txt",
            "curve-fit\\out.txt",
            "4",
            "python.exe",
            "curve-fit\\main.py"
        });
    }
}
