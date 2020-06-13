package io.fabric8.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.generator.model.GoPackage;
import io.fabric8.generator.model.GoPackageIndirectDependencies;
import io.fabric8.generator.model.GoScriptInput;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class GoScriptCreator {
    private static final String INPUT_FILE = "/generator/input.json";
    private static final String GO_TEMPLATE = "/generate-go-template.go";
    private static final String TARGET_GO_FILE = "cmd/generate/generate.go";
    private static final String PACKAGE_MAPPING = "__PACKAGE_MAPPING__";
    private static final String TYPES = "__TYPES__";
    private static final String IMPORTS = "__IMPORTS__";
    private static final String MANUAL_MAPPING = "__MANUAL_MAPPING__";
    private static final String INTERNAL_PACKAGE_MAPPING = "__INTERNAL_PACKAGE_MAPPING__";
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
            StringBuilder internalPackageMappingBuilder = new StringBuilder();
            StringBuilder manualTypeMappingBuilder = new StringBuilder();

            // Process all packages
            for (GoPackage goPackage : goScriptInput.getPackages()) {
                // Add imports
                if (goPackage.getGoPackage() != null) {
                    importBuilder.append(goPackage.getName())
                            .append(" \"").
                            append(goPackage.getGoPackage())
                            .append("\"\n");
                }

                // Add types
                if (goPackage.getTypes() != null && !goPackage.getTypes().isEmpty()) {
                    goPackage.getTypes().forEach(t ->
                                    typeBuilder.append("reflect.TypeOf(").append(goPackage.getName()).append(".").append(t.getName())
                                            .append("List{}):").append(t.getNamespaced() ? "  schemagen.Namespaced," : "  schemagen.Cluster," + "\n")
                            );
                }


                // Add mapping
                packageMappingBuilder.append("\"").append(goPackage.getGoPackage()).append("\": ")
                        .append("{JavaPackage: \"").append(goPackage.getJavaPackage()).append("\", ")
                        .append("ApiGroup: \"").append(goPackage.getApiGroup()).append("\", ")
                        .append("ApiVersion: \"").append(goPackage.getApiVersion()).append("\"},").append("\n");
            }

            // Process dependencies
            if (goScriptInput.getDependencies() != null) {
                for (GoPackageIndirectDependencies dependency : goScriptInput.getDependencies()) {
                    if (dependency.getName() != null && dependency.getGoPackage() != null) {
                        importBuilder.append(dependency.getName()).append(" \"").append(dependency.getGoPackage()).append("\"\n");
                    }
                    if (dependency.getMapping() != null && !dependency.getMapping().isEmpty()) {
                        for (Map.Entry<String, String> entry : dependency.getMapping().entrySet()) {
                            manualTypeMappingBuilder.append("reflect.TypeOf(");
                            manualTypeMappingBuilder.append(dependency.getName());
                            manualTypeMappingBuilder.append(".")
                                    .append(entry.getKey()).append("{}):              \"")
                                    .append(entry.getValue()).append("\",\n");
                        }
                    }

                }
            }

            if (goScriptInput.getInternalPackageMapping() != null) {
                for (Map.Entry<String, String> entry: goScriptInput.getInternalPackageMapping().entrySet()) {
                    internalPackageMappingBuilder.append("\"")
                            .append(entry.getKey()).append("\": ")
                            .append("\"").append(entry.getValue())
                            .append("\",");
                }
            }
            loadGoGenerateTemplateAndApply(importBuilder.toString(), typeBuilder.toString(), packageMappingBuilder.toString(), manualTypeMappingBuilder.toString(), internalPackageMappingBuilder.toString());
        }
    }

    private static void loadGoGenerateTemplateAndApply(String imports, String types, String mapping, String manualTypeMapping, String internalTypeMapping) throws IOException {
        File goTemplate = new File(GoScriptCreator.class.getResource(GO_TEMPLATE).getPath());
        if (goTemplate.exists()) {
            String fileAsStr = new String(Files.readAllBytes(goTemplate.toPath()));

            fileAsStr = searchAndReplaceTemplate(fileAsStr, IMPORTS, imports);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, TYPES, types);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, PACKAGE_MAPPING, mapping);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, MANUAL_MAPPING, manualTypeMapping);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, INTERNAL_PACKAGE_MAPPING, internalTypeMapping);

            File projectBaseDir = new File(System.getProperty("user.dir"));
            File goTargetFile = new File(projectBaseDir, TARGET_GO_FILE);

            System.out.println("Writing: " + goTargetFile.getAbsolutePath());
            try (FileWriter fooWriter = new FileWriter(goTargetFile, false)) {
                fooWriter.write(new String(fileAsStr.getBytes()));
            }
        }
    }

    private static String searchAndReplaceTemplate(String input, String template, String value) {
        if (value != null && !value.isEmpty()) {
            return input.replace(template, value);
        } else {
            return input.replace(template, "");
        }
    }
}
