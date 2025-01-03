package io.github.springstudent.dekstop.client.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FileUtilities {

    private FileUtilities() {
        throw new IllegalStateException();
    }

    public static long calculateTotalFileSize(List<File> files) throws IOException {
        long totalFilesSize = 0;
        for (File file : files) {
            totalFilesSize += calculateFileSize(file);
        }
        return totalFilesSize;
    }

    private static long calculateFileSize(File node) throws IOException {
        BasicFileAttributes basicFileAttributes = Files.readAttributes(node.toPath(), BasicFileAttributes.class);
        if (basicFileAttributes.isRegularFile()) {
            return basicFileAttributes.size();
        }
        try (Stream<Path> stream = Files.walk(node.toPath())) {
            return stream.filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        }
    }

    public static String separatorsToSystem(String path) {
        if (path == null) return null;
        if (File.separatorChar == '\\') {
            return path.replace('/', File.separatorChar);
        }
        return path.replace('\\', File.separatorChar);
    }

    public static List<File> getFiles(String dirPath) {
        List<File> fileList = new ArrayList<>();
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

}
