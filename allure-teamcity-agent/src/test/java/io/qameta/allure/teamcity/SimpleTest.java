/*
 *  Copyright 2016-2023 Qameta Software OÜ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
