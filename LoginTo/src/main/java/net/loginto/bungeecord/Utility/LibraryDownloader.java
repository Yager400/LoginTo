/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utility;

import net.byteflux.libby.BungeeLibraryManager;
import net.byteflux.libby.Library;
import net.md_5.bungee.api.plugin.Plugin;

public class LibraryDownloader {
    

    public static void Libs(Plugin plugin) {
        BungeeLibraryManager libManager = new BungeeLibraryManager(plugin);

        Library sqlite = Library.builder()
            .groupId("org.xerial")
            .artifactId("sqlite-jdbc")
            .version("3.51.0.0")
            .repository("https://repo1.maven.org/maven2/")
            .build();
        libManager.loadLibrary(sqlite);

        Library h2 = Library.builder()
            .groupId("com.h2database")
            .artifactId("h2")
            .version("2.4.240")
            .repository("https://repo1.maven.org/maven2/")
            .build();
        libManager.loadLibrary(h2);



    }
}
