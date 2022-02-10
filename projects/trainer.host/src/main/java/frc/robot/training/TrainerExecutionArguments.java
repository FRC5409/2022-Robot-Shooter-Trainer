package frc.robot.training;

import java.io.File;

public class TrainerExecutionArguments {
    public static final String INPUT_FILE_ARGUMENT_NAME = "in_path";
    public static final String OUTPUT_FILE_ARGUMENT_NAME = "out_path";
    public static final String DEGREE_ARGUMENT_NAME = "degree";

    public File inputFile;
    public File outputFile;
    public Integer degree;

    public String parse() {
        StringBuilder builder = new StringBuilder();

        if (inputFile != null) {
            builder.append('"')
                .append(INPUT_FILE_ARGUMENT_NAME)
                .append('=')
                .append(inputFile.getAbsolutePath())
                .append('"');
        }

        if (outputFile != null) {
            builder.append(" \"")
                .append(OUTPUT_FILE_ARGUMENT_NAME)
                .append('=')
                .append(outputFile.getAbsolutePath())
                .append('"');
        }

        if (degree != null) {
            builder.append(" \"")
                .append(DEGREE_ARGUMENT_NAME)
                .append('=')
                .append(degree)
                .append('"');
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{ \n");

        if (inputFile != null) {
            builder.append("\tinputFile  = ");
            builder.append(inputFile);
        }
        if (outputFile != null) {
            builder.append("\n\toutputFile = ");
            builder.append(outputFile);
        }
        if (degree != null) {
            builder.append("\n\tdegree     = ");
            builder.append(degree);
        }

        if (builder.length() > 3)
            builder.append('\n');

        builder.append('}');

        return builder.toString();
    }
}
