/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.dependencies;

import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;

public class CommonLibraries {

    public static void downloadLibraries(LibraryManager lib) {
        Library sqlite = Library.builder()
                .groupId("org.xerial")
                .artifactId("sqlite-jdbc")
                .version("3.51.0.0")
                .repository("https://repo1.maven.org/maven2/")
                .build();
        lib.loadLibrary(sqlite);

        Library mysql = Library.builder()
                .groupId("com{}mysql")
                .artifactId("mysql-connector-j")
                .version("8.2.0")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("com{}mysql", "com{}github{}yager400{}loginto{}libs{}mysql")
                .build();
        lib.loadLibrary(mysql);
    }

}
