/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.dependencies;

import com.github.yager400.loginto.common.data.dependencies.libbyextension.DependenciesManager;
import com.github.yager400.loginto.common.data.dependencies.libbyextension.Library;
import com.sun.jdi.InvalidTypeException;
import net.byteflux.libby.LibraryManager;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LibraryDownloader {

    public static void downloadLibraries(
            List<Library> libraries,
            HashMap<String, String> relocations,
            LibraryManager libraryManager,
            Path librariesPath,
            String dependencyFileVersion,
            List<String> groupIdToExclude
    ) throws Exception {
        downloadLibrariesForLibbyExtension(libraryManager);

        // Required relocations for the common project
        relocations.put("com{}zaxxer{}hikari", "com{}github{}yager400{}loginto{}libs{}hikari");
        relocations.put("com{}mysql", "com{}github{}yager400{}loginto{}libs{}mysql");
        relocations.put("org{}postgresql", "com{}github{}yager400{}loginto{}libs{}postgresql");
        relocations.put("org{}h2", "com{}github{}yager400{}loginto{}libs{}h2");

        // Required libraries for the common project
        libraries.add(new Library("com{}zaxxer:HikariCP:4.0.3"));
        libraries.add(new Library("org.xerial:sqlite-jdbc:3.51.0.0"));
        libraries.add(new Library("com{}mysql:mysql-connector-j:8.2.0"));
        libraries.add(new Library("org{}postgresql:postgresql:42.7.8"));
        libraries.add(new Library("com{}h2database:h2:2.4.240"));

        DependenciesManager dependenciesManager = new DependenciesManager(libraryManager, librariesPath);
        dependenciesManager.setDataFileVersion(dependencyFileVersion);
        dependenciesManager.addGroupsIdToExclude(groupIdToExclude);
        dependenciesManager.addGlobalRelocation(relocations);
        dependenciesManager.downloadLibraries(libraries);
    }

    /**
     * Use this only to download libraries while the plugin is running or if that library is not needed now
     */
    public static void downloadLibrariesAsync(
            List<Library> libraries,
            HashMap<String, String> relocations,
            LibraryManager libraryManager,
            Path librariesPath,
            String dependencyFileVersion,
            List<String> groupIdToExclude
    ) {
        CompletableFuture.runAsync(() -> {
            try {
                downloadLibraries(libraries, relocations, libraryManager, librariesPath, dependencyFileVersion, groupIdToExclude);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void downloadLibrariesForLibbyExtension(LibraryManager libraryManager) {
        net.byteflux.libby.Library mavenModels = net.byteflux.libby.Library.builder()
                .groupId("org{}apache{}maven")
                .artifactId("maven-model")
                .version("3.9.9")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("org{}apache{}maven{}model", "com{}github{}yager400{}loginto{}libs{}maven{}model")
                .relocate("org{}codehaus{}plexus", "com{}github{}yager400{}loginto{}libs{}plexus")
                .build();
        libraryManager.loadLibrary(mavenModels);

        net.byteflux.libby.Library codeHaus = net.byteflux.libby.Library.builder()
                .groupId("org{}codehaus{}plexus")
                .artifactId("plexus-utils")
                .version("3.5.1")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("org{}codehaus{}plexus", "com{}github{}yager400{}loginto{}libs{}plexus")
                .build();
        libraryManager.loadLibrary(codeHaus);

        net.byteflux.libby.Library snakeyaml = net.byteflux.libby.Library.builder()
                .groupId("org{}yaml")
                .artifactId("snakeyaml")
                .version("2.2")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("org{}yaml", "com{}github{}yager400{}loginto{}libs{}yaml")
                .build();
        libraryManager.loadLibrary(snakeyaml);
    }

}
