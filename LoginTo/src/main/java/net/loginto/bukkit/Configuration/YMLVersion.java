/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the MIT License.
See the LICENSE file for details.
*/

// Change the class package depending on your project
package net.loginto.bukkit.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class YMLVersion {
    
    private String version;
    private Plugin plugin;
    private String resource;
    private String versionKey;
    private boolean updateIfVersionKeyNull = true;

    /**
     * Creates a new YMLVersion builder instance.
     */
    public static YMLVersion builder() {
        return new YMLVersion();
    }

    /**
     * Sets the new version to be applied to the YML file.
     * @param version the new version string
     */
    public YMLVersion version(String version) {

        this.version = version;

        return this;
    }

    /**
     * Sets the plugin instance that owns the resource.
     * @param plugin the plugin instance
     */
    public YMLVersion plugin(Plugin plugin) {
        
        this.plugin = plugin;

        return this;
    }

    /**
     * Sets the name of the configuration resource file.
     * If the file is in a folder, include the relative path as well.
     * @param resourceName the resource file name or path
     */
    public YMLVersion resource(String resourceName) {

        this.resource = resourceName;

        return this;
    }

    /**
     * Sets the key in the YML file used to track the version.
     * @param key the version key in the config
     */
    public YMLVersion versionKey(String key) {

        this.versionKey = key;

        return this;
    }

    /**
     * Determines whether the YML file should be updated even if the version key is missing or null.
     * This option is optional and defaults to true.
     * 
     * @param update true to update even if the version key is null, false to skip the update
     */
    public YMLVersion updateIfVersionKeyNull(boolean update) {

        this.updateIfVersionKeyNull = update;

        return this;
    }

    /**
     * Builds and applies the YML update according to the configured parameters.
     * @throws IOException if an error occurs saving the YML file
     * @throws IllegalArgumentException if required fields are missing
     */
    public Builder build() throws IOException {

        return new Builder();

    }

    class Builder {

        public Builder() throws IOException {

            Objects.requireNonNull(plugin, "Plugin instance cannot be null");
            Objects.requireNonNull(version, "Version must be specified");
            Objects.requireNonNull(versionKey, "Version key must be specified");
            Objects.requireNonNull(resource, "Resource must be specified");

            HashMap<String, Object> oldFileContent = new HashMap<String, Object>();

            File file = new File(plugin.getDataFolder(), resource);

            if (!file.exists()) {
                plugin.saveResource(resource, false);
                return;
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (String key : config.getKeys(true)) {
                if (!config.isConfigurationSection(key)) {
                    oldFileContent.put(key, config.get(key));
                }
            }

            String oldVer = config.getString(versionKey);
            if (oldVer == null && !updateIfVersionKeyNull) return;
            if (Objects.equals(oldVer, version)) return;


            plugin.saveResource(resource, true);

            config = YamlConfiguration.loadConfiguration(file);

            for (String key : oldFileContent.keySet()) {
                if (config.contains(key)) {
                    config.set(key, oldFileContent.get(key));
                } else {
                    config.set("old." + key, oldFileContent.get(key));
                }
            }

            config.set(versionKey, version);

            config.save(file);
        }
    }
}