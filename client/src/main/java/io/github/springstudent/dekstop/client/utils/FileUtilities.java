package io.github.springstudent.dekstop.client.utils;

import cn.hutool.crypto.digest.DigestUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
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

    public static String bufferedImgMd5(BufferedImage bufferedImage) throws IOException {
        // 将BufferedImage转换为字节数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String md5 = DigestUtil.md5Hex(imageBytes);
        return md5;
    }
}
