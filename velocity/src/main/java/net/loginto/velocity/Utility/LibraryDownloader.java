/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Utility;

import net.byteflux.libby.LibraryManager;
import net.byteflux.libby.VelocityLibraryManager;
import net.loginto.velocity.LoginTo;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.velocitypowered.api.proxy.ProxyServer;

import net.byteflux.libby.Library;

public class LibraryDownloader {
    

    public static void Libs(LoginTo plugin, Logger logger, Path dataDirectory, ProxyServer server) {
        
        @SuppressWarnings("rawtypes")
        VelocityLibraryManager libManager = new VelocityLibraryManager<>(
            logger,
            dataDirectory,
            server.getPluginManager(),
            plugin
        );

        downloadKyoriDependency(libManager);

        Library sqlite = Library.builder()
            .groupId("org.xerial")
            .artifactId("sqlite-jdbc")
            .version("3.41.2.1")
            .repository("https://repo1.maven.org/maven2/")
            .build();
        libManager.loadLibrary(sqlite);

        Library h2 = Library.builder()
            .groupId("com{}h2database")
            .artifactId("h2")
            .version("2.4.240")
            .repository("https://repo1.maven.org/maven2/")
            .relocate("org{}h2", "net{}loginto{}libs{}h2")
            .build();
        libManager.loadLibrary(h2);

        Library mysql = Library.builder()
            .groupId("com{}mysql")
            .artifactId("mysql-connector-j")
            .version("8.2.0")
            .repository("https://repo1.maven.org/maven2/")
            .relocate("com{}mysql", "net{}loginto{}libs{}mysql")
            .build();
        libManager.loadLibrary(mysql);

    }

    private static void downloadKyoriDependency(VelocityLibraryManager libManager) {
        //Kyori
        Library kyoriAPI = Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-api")
                .version("4.26.1")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("net{}kyori", "net{}loginto{}libs{}kyori")
                .build();
        libManager.loadLibrary(kyoriAPI);

        Library kyoriNBT = Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-nbt")
                .version("4.26.1")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("net{}kyori", "net{}loginto{}libs{}kyori")
                .build();
        libManager.loadLibrary(kyoriNBT);

        Library kyoriKEY = Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-key")
                .version("4.26.1")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("net{}kyori", "net{}loginto{}libs{}kyori")
                .build();
        libManager.loadLibrary(kyoriKEY);

        Library kyoriExamination = Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-api")
                .version("1.3.0")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("net{}kyori", "net{}loginto{}libs{}kyori")
                .build();
        libManager.loadLibrary(kyoriExamination);

        Library kyoriExaminationString = Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-string")
                .version("1.3.0")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("net{}kyori", "net{}loginto{}libs{}kyori")
                .build();
        libManager.loadLibrary(kyoriExaminationString);

        Library kyoriMiniMessage = Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-legacy")
                .version("4.26.1")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("net{}kyori", "net{}loginto{}libs{}kyori")
                .build();
        libManager.loadLibrary(kyoriMiniMessage);

        Library kyoriLegacyTextSerializer = Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.26.1")
                .repository("https://repo1.maven.org/maven2/")
                .relocate("net{}kyori", "net{}loginto{}libs{}kyori")
                .build();
        libManager.loadLibrary(kyoriLegacyTextSerializer);
    }
}
