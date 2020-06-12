package io.fabric8.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.generator.model.GoPackage;
import io.fabric8.generator.model.GoScriptInput;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class GoScriptCreator {
    private static final String INPUT_FILE = "/generator/input.json";
    private static final String GO_TEMPLATE = "/generate-go-template.go";
    private static final String TARGET_GO_FILE = "cmd/generate/generate.go";
    private static final String PACKAGE_MAPPING = "__PACKAGE_MAPPING__";
    private static final String TYPES = "__TYPES__";
    private static final String IMPORTS = "__IMPORTS__";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        File inputFile = new File(GoScriptCreator.class.getResource(INPUT_FILE).getPath());
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("No input file found!");
        }

        GoScriptInput goScriptInput = objectMapper.readValue(inputFile, GoScriptInput.class);
        processInput(goScriptInput);
    }

    private static void processInput(GoScriptInput goScriptInput) throws IOException {
        if (goScriptInput != null) {
            StringBuilder importBuilder = new StringBuilder();
            StringBuilder typeBuilder = new StringBuilder();
            StringBuilder packageMappingBuilder = new StringBuilder();

            for (GoPackage goPackage : goScriptInput.getPackages()) {
                // Add imports
                if (goPackage.getGoPackage() != null) {
                    importBuilder.append(goPackage.getName() + " \"" + goPackage.getGoPackage() + "\"");
                }

                // Add types
                if (goPackage.getTypes() != null && !goPackage.getTypes().isEmpty()) {
                    goPackage.getTypes().forEach(t ->
                            typeBuilder.append("reflect.TypeOf(" + goPackage.getName() + "." + t.getName() + "List{}):" +
                                    (t.getNamespaced() ? "  schemagen.Namespaced," : "  schemagen.Cluster,"))
                            );
                }


                // Add mapping
                packageMappingBuilder.append("\""+ goPackage.getGoPackage()+"\": " +
                        "{JavaPackage: \""+ goPackage.getJavaPackage() +"\", " +
                        "ApiGroup: \""+ goPackage.getApiGroup() +"\", " +
                        "ApiVersion: \""+goPackage.getApiVersion()+"\"},");
            }
            loadGoGenerateTemplateAndApply(importBuilder.toString(), typeBuilder.toString(), packageMappingBuilder.toString());
        }
    }

    private static void loadGoGenerateTemplateAndApply(String imports, String types, String mapping) throws IOException {
        File goTemplate = new File(GoScriptCreator.class.getResource(GO_TEMPLATE).getPath());
        if (goTemplate.exists()) {
            String fileAsStr = new String(Files.readAllBytes(goTemplate.toPath()));

            fileAsStr = fileAsStr.replace(IMPORTS, imports);
            fileAsStr = fileAsStr.replace(TYPES, types);
            fileAsStr = fileAsStr.replace(PACKAGE_MAPPING, mapping);

            File projectBaseDir = new File(System.getProperty("user.dir"));
            File goTargetFile = new File(projectBaseDir, TARGET_GO_FILE);

            System.out.println("Writing: " + goTargetFile.getAbsolutePath());
            FileWriter fooWriter = new FileWriter(goTargetFile, false);
            fooWriter.write(new String(fileAsStr.getBytes()));
            fooWriter.close();
        }
    }
}
