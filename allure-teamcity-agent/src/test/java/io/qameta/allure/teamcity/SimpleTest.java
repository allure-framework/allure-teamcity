package io.qameta.allure.teamcity;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleTest {

    public static void main(String[] args) throws IOException {
        Path allureArchive = Paths.get("/Users/eroshenkoam/Downloads/allure-report.zip");
        Path base = Paths.get("/Users/eroshenkoam/Downloads/allure-report");

        List<Path> allureReportFiles = Files.walk(Paths.get("/Users/eroshenkoam/Downloads/allure-report"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        zipViaApacheCompress(allureArchive, base, allureReportFiles);
    }

    private static void zipViaApacheCompress(Path archive, Path base, List<Path> files) throws IOException {
        try (ArchiveOutputStream output = new ZipArchiveOutputStream(new FileOutputStream(archive.toFile()))) {
            for (Path file : files) {
                String entryName = base.toAbsolutePath().relativize(file).toString();
                ArchiveEntry entry = output.createArchiveEntry(file.toFile(), entryName);
                output.putArchiveEntry(entry);
                try (InputStream i = Files.newInputStream(file)) {
                    IOUtils.copy(i, output);
                }
                output.closeArchiveEntry();
            }
            output.finish();
        }
    }

}
