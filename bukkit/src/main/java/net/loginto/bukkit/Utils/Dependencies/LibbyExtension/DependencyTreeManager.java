/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Dependencies.LibbyExtension;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DependencyTreeManager {

    public void resolveDependencyTree(Library library) {

        Model model = fetchModel(library);
        if (model == null) return;

        List<String> repositories = readRepositories(model);
        if (!repositories.contains(library.repo)) {
            repositories.add(0, library.repo);
        }
        if (!repositories.contains(Library.Resository.MAVEN.getURL())) {
            repositories.add(Library.Resository.MAVEN.getURL());
        }

        for (String depGav : readDependencies(model)) {

            if (depGav.endsWith(":unknown")) continue;

            Library dep = findInRepos(depGav, repositories);
            if (dep != null) {
                try {
                    DependencyManager.downloadSingleLibrary(dep);
                } catch (Exception ignored) {

                }
            }
        }
    }

    private Library findInRepos(String gav, List<String> repos) {
        for (String repo : repos) {
            try {
                Library candidate = new Library(gav, repo);
                String pomUrl = buildPomUrl(candidate);
                HttpURLConnection conn = (HttpURLConnection) new URL(pomUrl).openConnection();
                conn.setRequestMethod("HEAD");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                int code = conn.getResponseCode();
                conn.disconnect();
                if (code == 200) return candidate;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private Model fetchModel(Library lib) {
        try {
            String pomUrl = buildPomUrl(lib);
            try (InputStream in = new URL(pomUrl).openStream()) {
                return new MavenXpp3Reader().read(in);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> readDependencies(Model model) {
        List<String> list = new ArrayList<>();
        for (org.apache.maven.model.Dependency dep : model.getDependencies()) {

            String scope = dep.getScope();
            if ("test".equals(scope) || "provided".equals(scope)) continue;

            String version = dep.getVersion();
            if (version == null) version = "unknown";

            list.add(dep.getGroupId() + ":" + dep.getArtifactId() + ":" + version);
        }
        return list;
    }

    private List<String> readRepositories(Model model) {
        List<String> list = new ArrayList<>();
        for (Repository repo : model.getRepositories()) {
            String url = repo.getUrl();
            if (url != null && !url.isEmpty() && !list.contains(url)) {
                list.add(url);
            }
        }
        return list;
    }

    private String buildPomUrl(Library lib) {
        String base = lib.repo.endsWith("/") ? lib.repo : lib.repo + "/";
        String groupPath = lib.groupId.replace(".", "/");
        return base
                + groupPath + "/"
                + lib.artifactId + "/"
                + lib.version + "/"
                + lib.artifactId + "-" + lib.version + ".pom";
    }
}