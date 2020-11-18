package io.fabric8.generator;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelGeneratorServiceTest {
    @Test
    void testGenerate() throws IOException {
        // Given
        File inputFile = new File(getClass().getResource("/sample-controller-input.json").getFile());
        File targetDir = Files.createTempDirectory("test-generate").toFile();

        // When
        ModelGeneratorService modelGeneratorService = new ModelGeneratorService();
        modelGeneratorService.generate(inputFile, false, targetDir);

        // Then
        assertNotNull(targetDir);
        assertTrue(Objects.requireNonNull(targetDir.listFiles()).length > 0);
    }
}
