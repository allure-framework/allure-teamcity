package ru.yandex.qatools.allure.teamcity;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;

/**
 * eroshenkoam
 * 6/17/14
 */
public class FileUtils {

    private FileUtils() {

    }

    public static File[] findFilesByMask(File file, String[] mask) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(file);
        scanner.setIncludes(mask);
        scanner.setCaseSensitive(false);
        scanner.scan();

        String[] paths = scanner.getIncludedDirectories();
        File[] files = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            files[i] = (new File(file, paths[i]));
        }
        return files;
    }

}
