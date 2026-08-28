/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

public class FilesManager {

    public static void saveFile(String filePath, Path fileDestination, boolean replaceExisting) {
        try (InputStream stream = FilesManager.class.getClassLoader().getResourceAsStream(filePath)) {
            if (stream == null) {
                throw new IOException(filePath + " does not exists");
            }

            if (!replaceExisting) {
                if (!fileDestination.toFile().exists()) {
                    Files.copy(stream, fileDestination);
                }
            } else {
                if (fileDestination.toFile().exists()) {
                    fileDestination.toFile().delete();
                }
                Files.copy(stream, fileDestination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFiles(Map<String, Path> files, boolean replaceExisting) {
        for (String filePath : files.keySet()) {
            saveFile(filePath, files.get(filePath), replaceExisting);
        }
    }

    public static void saveFilesAsync(Map<String, Path> files, boolean replaceExisting) {
        CompletableFuture.runAsync(() -> {
            saveFiles(files, replaceExisting);
        });
    }

    public static void downloadRockYou(Path fileDestination) {
        CompletableFuture.runAsync(() -> {

            URI rockyouURL = URI.create("https://weakpass.com/download/90/rockyou.txt.gz");
            File txtFile = fileDestination.toFile();

            if (txtFile.exists()) {
                return;
            }

            try (GZIPInputStream gzipIn = new GZIPInputStream(rockyouURL.toURL().openStream());
                 FileOutputStream fileOut = new FileOutputStream(txtFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = gzipIn.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    public static String getPluginDataFolderName() {
        return "_pluginData";
    }

    public static void makePluginDataFolder(Path pluginDataPath) {
        File file = Paths.get(pluginDataPath.toFile().getAbsolutePath(), getPluginDataFolderName()).toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void updateYamlFile(File file, String filePath, String version, ValueKey versionKey) throws IOException {
        try (YamlReader reader = new YamlReader(file)) {
            if (version.equals(reader.getString(versionKey))) {
                return;
            }
        } catch (Exception ignored) {}
        File destination = new File(file.getParentFile().getAbsolutePath(), file.getName() + ".old");
        Files.move(
                file.toPath(),
                destination.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
        saveFile(filePath, file.toPath(), false);
    }

    public static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
    }

}
