package io.fabric8.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.generator.model.GoPackage;
import io.fabric8.generator.model.GoPackageIndirectDependencies;
import io.fabric8.generator.model.GoScriptInput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModelGeneratorService {
    private static final Logger logger = Logger.getLogger(GoScriptCreator.class.getSimpleName());
    private static final String EXTENSION_TEMPLATE_URL = "https://github.com/r0haaaan/fabric8-extension-model-generator-template.git";
    private static final String GO_GENERATOR = "generator/";
    private static final String GO_TEMPLATE = "generator/cmd/generate/generate.go";
    private static final String PACKAGE_MAPPING = "__PACKAGE_MAPPING__";
    private static final String PROVIDED_PACKAGE_MAPPING = "__PROVIDED_PACKAGE_MAPPING__";
    private static final String TYPES = "__TYPES__";
    private static final String IMPORTS = "__IMPORTS__";
    private static final String MANUAL_MAPPING = "__MANUAL_MAPPING__";
    private static final String INTERNAL_PACKAGE_MAPPING = "__INTERNAL_PACKAGE_MAPPING__";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void generate(File inputFile) throws IOException {
        generate(inputFile, true, null);
    }

    void generate(File inputFile, boolean shouldRunGoBuild, File targetDirFile) throws IOException {
        GoScriptInput goScriptInput = objectMapper.readValue(inputFile, GoScriptInput.class);
        processInput(goScriptInput, targetDirFile);
        if (shouldRunGoBuild) {
            runBashCommand("make -C " + goScriptInput.getName() + "/" + GO_GENERATOR);
        }
    }

    void processInput(GoScriptInput goScriptInput, File targetDirectoryToCreate) throws IOException {
        if (goScriptInput != null) {
            StringBuilder importBuilder = new StringBuilder();
            StringBuilder typeBuilder = new StringBuilder();
            StringBuilder packageMappingBuilder = new StringBuilder();
            StringBuilder internalPackageMappingBuilder = new StringBuilder();
            StringBuilder manualTypeMappingBuilder = new StringBuilder();
            StringBuilder providedPackageMappingBuilder = new StringBuilder();

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
                            typeBuilder.append("reflect.TypeOf(").append(goPackage.getName()).append(".")
                                    .append(t.getName()).append("List").append("{}):")
                                    .append(t.getNamespaced() ? "  schemagen.Namespaced," : "  schemagen.Cluster," + "\n")
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

            if (goScriptInput.getProvidedPackageMapping() != null) {
                for (Map.Entry<String, String> entry: goScriptInput.getProvidedPackageMapping().entrySet()) {
                    providedPackageMappingBuilder.append("\"")
                            .append(entry.getKey()).append("\": ")
                            .append("\"").append(entry.getValue())
                            .append("\",");
                }
            }
            String targetDirPath = targetDirectoryToCreate != null ?
                    new File(targetDirectoryToCreate, goScriptInput.getName()).getAbsolutePath() :
                    goScriptInput.getName();
            File extensionDir = downloadExtensionTemplate(targetDirPath);
            loadGoGenerateTemplateAndApply(extensionDir, importBuilder.toString(), typeBuilder.toString(), packageMappingBuilder.toString(), manualTypeMappingBuilder.toString(), internalPackageMappingBuilder.toString(), providedPackageMappingBuilder.toString());
        }
    }

    private void processInput(GoScriptInput goScriptInput) throws IOException {
        processInput(goScriptInput, null);
    }

    private void loadGoGenerateTemplateAndApply(File extensionDir, String imports, String types, String mapping, String manualTypeMapping, String internalTypeMapping, String providedPackageMapping) throws IOException {
        File goTemplate = new File(extensionDir, GO_TEMPLATE);
        if (goTemplate.exists()) {
            String fileAsStr = new String(Files.readAllBytes(goTemplate.toPath()));

            fileAsStr = searchAndReplaceTemplate(fileAsStr, IMPORTS, imports);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, TYPES, types);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, PACKAGE_MAPPING, mapping);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, PROVIDED_PACKAGE_MAPPING, providedPackageMapping);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, MANUAL_MAPPING, manualTypeMapping);
            fileAsStr = searchAndReplaceTemplate(fileAsStr, INTERNAL_PACKAGE_MAPPING, internalTypeMapping);

            File goTargetFile = new File(extensionDir, GO_TEMPLATE);

            writeStringToFile(fileAsStr, goTargetFile);
        }
    }

    private void writeStringToFile(String fileAsStr, File file) throws IOException {
        logger.info("Writing: " + file.getAbsolutePath());
        try (FileWriter fooWriter = new FileWriter(file, false)) {
            fooWriter.write(new String(fileAsStr.getBytes()));
        }
    }

    private String searchAndReplaceTemplate(String input, String template, String value) {
        if (value != null && !value.isEmpty()) {
            return input.replace(template, value);
        } else {
            return input.replace(template, "");
        }
    }

    private void runBashCommand(String command) {
        try {
            logger.info("Running bash command : " + command);
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line =  stdInput.readLine()) != null) {
                logger.info(line);
            }

            while ((line = stdOutput.readLine()) != null) {
                logger.log(Level.SEVERE, line);
            }
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException interruptedEx) {
            Thread.currentThread().interrupt();
            interruptedEx.printStackTrace();
        }
    }

    private File downloadExtensionTemplate(String name) {
        runBashCommand("git clone " + EXTENSION_TEMPLATE_URL + " " + name);
        return new File(name);
    }
}
