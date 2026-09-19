/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.files;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
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

    public static InputStreamReader getFileInputStreamReader(String filePath) {
        return new InputStreamReader(
                Objects.requireNonNull(FilesManager.class.getClassLoader().getResourceAsStream(filePath)),
                StandardCharsets.UTF_8
        );
    }

    public static void downloadRockYou(Path pluginDataFolder) {
        CompletableFuture.runAsync(() -> {

            Path fileDestination = Paths.get(pluginDataFolder.toFile().getAbsolutePath(), FilesManager.getPluginDataFolderName(), "rockyou.txt.gz");
            URI rockyouURL = URI.create("https://weakpass.com/download/90/rockyou.txt.gz");
            File file = fileDestination.toFile();

            if (file.exists()) {
                return;
            }

            try (InputStream in = rockyouURL.toURL().openStream();
                 FileOutputStream fileOut = new FileOutputStream(file)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
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

    public static void updateYamlFile(File file, String filePath, ValueKey versionKey) throws IOException {
        try (YamlReader diskConfigReader = new YamlReader(file);
             YamlReader ramConfigReader = new YamlReader(FilesManager.getFileInputStreamReader(filePath))) {
            String diskVersion = diskConfigReader.getString(versionKey);
            String ramVersion = ramConfigReader.getString(versionKey);
            if (diskVersion.equals(ramVersion)) {
                return;
            }
        } catch (IOException ignored) {}

        File destination = new File(file.getParentFile().getAbsolutePath(), file.getName() + ".old");
        Files.move(
                file.toPath(),
                destination.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
        saveFile(filePath, file.toPath(), false);
    }

}
