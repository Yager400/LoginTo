/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Dependencies;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.LibraryManager;
import net.loginto.bukkit.Utils.Dependencies.LibbyExtension.DependencyManager;
import net.loginto.bukkit.Utils.Dependencies.LibbyExtension.Library;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Libraries {

    public static void loadLibs(Plugin plugin) {

        Libraries.downloadRequiredLibraries(new BukkitLibraryManager(plugin));

        DependencyManager dependencyManager = new DependencyManager(plugin);

        dependencyManager.setDataFileVersion("1");

        HashMap<String, String> relocations = new HashMap<>();
        relocations.put("com{}zaxxer{}hikari", "net{}loginto{}libs{}hikari");
        relocations.put("com{}mysql", "net{}loginto{}libs{}mysql");
        relocations.put("org{}postgresql", "net{}loginto{}libs{}postgresql");
        relocations.put("org{}h2", "net{}loginto{}libs{}h2");
        relocations.put("com{}google{}zxing", "net{}loginto{}libs{}zxing");
        relocations.put("com{}warrenstrange{}googleauth", "net{}loginto{}libs{}googleauth");
        relocations.put("net{}kyori", "net{}loginto{}libs{}kyori");
        dependencyManager.addGlobalRelocation(relocations);

        //Skip big packages like io.netty (the server already includes them, and they are like 30+ MB)
        List<String> groupsIdToExclude = new ArrayList<>();
        groupsIdToExclude.add("io.netty");
        groupsIdToExclude.add("io.projectreactor");
        groupsIdToExclude.add("commons-logging");
        groupsIdToExclude.add("avalon-framework");
        groupsIdToExclude.add("org.checkerframework");
        dependencyManager.addGroupsIdToExclude(groupsIdToExclude);

        try {
            List<Library> libraries = new ArrayList<>();
            libraries.add(new Library("com{}zaxxer:HikariCP:4.0.3"));
            libraries.add(new Library("org.xerial:sqlite-jdbc:3.51.0.0"));
            libraries.add(new Library("com{}mysql:mysql-connector-j:8.2.0"));
            libraries.add(new Library("org{}postgresql:postgresql:42.7.8"));
            libraries.add(new Library("com{}h2database:h2:2.4.240"));
            libraries.add(new Library("com{}google{}zxing:core:3.5.3"));
            libraries.add(new Library("com{}warrenstrange:googleauth:1.5.0"));
            libraries.add(new Library("com.github.retrooper:packetevents-api:2.12.1", Library.Resository.CODEMC));
            libraries.add(new Library("com.github.retrooper:packetevents-spigot:2.12.1", Library.Resository.CODEMC));
            libraries.add(new Library("net{}kyori:adventure-text-serializer-legacy:4.26.1"));
            libraries.add(new Library("net{}kyori:adventure-text-minimessage:4.26.1"));
            libraries.add(new Library("net{}kyori:adventure-platform-api:4.3.4"));
            libraries.add(new Library("net{}kyori:adventure-platform-bukkit:4.3.4"));
            libraries.add(new Library("net{}kyori:adventure-api:4.26.1"));

            plugin.getLogger().info("Library downloader started, this might take a while if you connection is slow");

            dependencyManager.downloadLibraries(libraries);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().severe(e.getMessage());
        }

        plugin.getLogger().info("Libraries downloaded, ready to start the plugin");
    }

    private static void downloadRequiredLibraries(LibraryManager libManager) {
        net.byteflux.libby.Library mavenModels = net.byteflux.libby.Library.builder()
                .groupId("org{}apache{}maven")
                .artifactId("maven-model")
                .version("3.9.9")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("org{}apache{}maven{}model", "net{}loginto{}libs{}maven{}model")
                .build();
        libManager.loadLibrary(mavenModels);

        net.byteflux.libby.Library codeHaus = net.byteflux.libby.Library.builder()
                .groupId("org{}codehaus{}plexus")
                .artifactId("plexus-utils")
                .version("3.5.1")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("org{}codehaus{}plexus{}util", "net{}loginto{}libs{}plexus{}util")
                .build();
        libManager.loadLibrary(codeHaus);
    }
}