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
package io.qameta.allure.teamcity.utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * eroshenkoam.
 * 11.04.17
 */
public final class ZipUtils {

    private ZipUtils() {
    }

    public static List<ZipEntry> listEntries(final ZipFile zip,
                                             final String path) {
        final Enumeration<? extends ZipEntry> entries = zip.entries();
        final List<ZipEntry> files = new ArrayList<>();
        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            if (entry.getName().startsWith(path)) {
                files.add(entry);
            }
        }
        return files;
    }

    public static void zip(final Path archive,
                           final Path base,
                           final List<Path> files) throws IOException {
        try (ArchiveOutputStream output = new ZipArchiveOutputStream(Files.newOutputStream(archive))) {
            for (Path file : files) {
                final String entryName = base.toAbsolutePath().relativize(file).toString();
                final ArchiveEntry entry = output.createArchiveEntry(file.toFile(), entryName);
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
