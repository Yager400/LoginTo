/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Dependencies.LibbyExtension;

public class Library {

    public final String groupId;
    public final String artifactId;
    public final String version;
    public final String repo;
    public final String originalDepe;

    public Library(String dependency, Object repoUrl) throws IllegalArgumentException {
        if (repoUrl instanceof String) {
            this.repo = (String) repoUrl;
        } else if (repoUrl instanceof Library.Resository){
            this.repo = ((Resository) repoUrl).getURL();
        } else {
            throw new IllegalArgumentException("Invalid repoURL object");
        }

        if (dependency == null) {
            throw new IllegalArgumentException("Dependency is null");
        }

        dependency = dependency.replace("{}", ".");

        String[] splitDependency = dependency.split(":");

        if (splitDependency.length != 3) {
            throw new IllegalArgumentException(dependency + " is invalid, must be like this: groupId:artifactId:version");
        }

        this.groupId = splitDependency[0];
        this.artifactId = splitDependency[1];
        this.version = splitDependency[2];

        this.originalDepe = dependency;
    }

    public Library(String dependency) throws IllegalArgumentException {
        this(dependency, Resository.MAVEN);
    }

    public enum Resository {
        MAVEN("https://repo1.maven.org/maven2/"),
        JITPACK("https://jitpack.io"),
        CODEMC("https://repo.codemc.io/repository/maven-releases/");

        public final String s;
        Resository(String s) {
            this.s = s;
        }
        public String getURL() {
            return s;
        }
    }

}
