/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit;

import org.bukkit.plugin.Plugin;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;

public class LibraryDownloader {
    

    public static void Libs(Plugin plugin) {
        BukkitLibraryManager libManager = new BukkitLibraryManager(plugin);

        Library hikari = Library.builder()
            .groupId("com.zaxxer")
            .artifactId("HikariCP")
            .version("4.0.3")
            .repository("https://repo1.maven.org/maven2/")
            .build();
        libManager.loadLibrary(hikari);

        Library sqlite = Library.builder()
            .groupId("org.xerial")
            .artifactId("sqlite-jdbc")
            .version("3.51.0.0")
            .repository("https://repo1.maven.org/maven2/")
            .build();
        libManager.loadLibrary(sqlite);

        Library mysql = Library.builder()
            .groupId("com.mysql")
            .artifactId("mysql-connector-j")
            .version("8.2.0")
            .repository("https://repo1.maven.org/maven2/")
            .build();
        libManager.loadLibrary(mysql);

        Library postgreSQL = Library.builder()
            .groupId("org.postgresql")
            .artifactId("postgresql")
            .version("42.7.8")
            .repository("https://repo1.maven.org/maven2/")
            .build();
        libManager.loadLibrary(postgreSQL);

        Library h2 = Library.builder()
            .groupId("com.h2database")
            .artifactId("h2")
            .version("2.4.240")
            .repository("https://repo1.maven.org/maven2/")
            .build();
        libManager.loadLibrary(h2);
    }
}
