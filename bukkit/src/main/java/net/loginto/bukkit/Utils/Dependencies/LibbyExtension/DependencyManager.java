/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Dependencies.LibbyExtension;

import com.sun.jdi.InvalidTypeException;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.LibraryManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DependencyManager {

    private static DependencyTreeManager depTreeManager = new DependencyTreeManager();

    private static HashMap<String, String> relocations = null;
    private static List<String> groupsIdToExclude = null;

    protected static Plugin plugin;
    private static LibraryManager libManager;

    private static File librariesSavingFile;
    private static String dataFileVersion = "1";

    public DependencyManager(Plugin plugin1) {
        plugin = plugin1;
        libManager = new BukkitLibraryManager(plugin);
        File libPath = Paths.get(plugin.getDataFolder().getAbsolutePath(), "lib").toFile();
        librariesSavingFile = new File(libPath, "data.yml");
        try {
            libPath.mkdirs();
            librariesSavingFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addGlobalRelocation(HashMap<String, String> relocations1) {
        relocations = relocations1;
    }

    public void addGroupsIdToExclude(List<String> groupsIdToExclude1) {
        groupsIdToExclude = groupsIdToExclude1;
    }

    protected static void downloadSingleLibrary(Library lib) {
        downloadSingleLibraryNoTree(lib);

        depTreeManager.resolveDependencyTree(lib);
    }

    protected static void downloadSingleLibraryNoTree(Library lib) {
        if (groupsIdToExclude != null && groupsIdToExclude.contains(lib.groupId)) {
            return;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(librariesSavingFile);

            String art = lib.artifactId.replace(".", "-");

            config.set("lib." + art + ".groupId", lib.groupId);
            config.set("lib." + art + ".artifactId", lib.artifactId);
            config.set("lib." + art + ".version", lib.version);
            config.set("lib." + art + ".repo", lib.repo);

            config.save(librariesSavingFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        net.byteflux.libby.Library.Builder builder = net.byteflux.libby.Library.builder()
                .groupId(lib.groupId)
                .artifactId(lib.artifactId)
                .version(lib.version)
                .repository(lib.repo);

        for (String s : relocations.keySet()) {
            builder.relocate(s, relocations.get(s));
        }

        libManager.loadLibrary(builder.build());
    }

    public void setDataFileVersion(String version) {
        DependencyManager.dataFileVersion = version;
    }

    public void downloadLibraries(List<Library> libraries) throws InvalidTypeException, IOException {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(librariesSavingFile);
        String dataVersion = config.getString("version");

        ConfigurationSection configList = config.getConfigurationSection("lib");

        if (configList != null && Objects.equals(dataVersion, dataFileVersion)) {
            for (Object s : configList.getKeys(false)) {
                String group = config.getString("lib." + s + ".groupId");
                String artifactId = config.getString("lib." + s + ".artifactId");
                String version = config.getString("lib." + s + ".version");
                String repo = config.getString("lib." + s + ".repo");

                downloadSingleLibraryNoTree(new Library(String.format("%s:%s:%s", group, artifactId, version), repo));
            }
            return;
        } else {
            if (librariesSavingFile.exists()) {
                librariesSavingFile.delete();
                librariesSavingFile.createNewFile();
            } else {
                librariesSavingFile.createNewFile();
            }
            config.set("version", dataFileVersion);
            config.save(librariesSavingFile);
        }

        for (Library lib : libraries) {
            downloadSingleLibrary(lib);
        }
    }
}
