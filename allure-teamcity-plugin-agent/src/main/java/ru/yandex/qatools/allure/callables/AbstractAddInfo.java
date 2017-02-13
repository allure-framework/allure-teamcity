package ru.yandex.qatools.allure.callables;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abstract class to provide an additional information for reports.
 */
public abstract class AbstractAddInfo {

    public Path invoke(Path outputDirectory) throws IOException {
        Files.createDirectories(outputDirectory);
        Path testRun = outputDirectory.resolve(getFileName());
        try (Writer writer = Files.newBufferedWriter(testRun, StandardCharsets.UTF_8)) {
            JSONObject.fromObject(getData())
                    .write(writer)
                    .flush();
        }
        return testRun;
    }

    protected abstract Object getData();

    protected abstract String getFileName();

}
