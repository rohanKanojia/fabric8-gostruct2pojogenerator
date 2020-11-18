package io.fabric8.generator;

import java.io.File;
import java.io.IOException;

public class GoScriptCreator {
    public static void main(String[] args) throws IOException {
        String inputFilePath = System.getProperty("inputFile");
        if (inputFilePath == null) {
            throw new IllegalArgumentException("Usage: java -jar xyz.jar -DinputFile=input.json");
        }

        File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("No input file found!");
        }
        ModelGeneratorService modelGeneratorService = new ModelGeneratorService();
        modelGeneratorService.generate(inputFile);
    }
}
