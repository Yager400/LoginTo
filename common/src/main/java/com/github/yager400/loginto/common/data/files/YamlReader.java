/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.files;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YamlReader implements AutoCloseable {

    private Object dataToRead;
    private File file;
    private Yaml yaml;
    private Map<String, Object> data;

    public YamlReader(File file) {
        try {
            this.dataToRead = new FileReader(file);
            this.file = file;
            parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public YamlReader(InputStreamReader reader) {
        this.dataToRead = reader;
        parse();
    }

    private void parse() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        this.yaml = new Yaml(options);
        if (dataToRead instanceof InputStreamReader) {
            this.data = yaml.load((InputStreamReader) dataToRead);
        } else {
            this.data = yaml.load((Reader) dataToRead);
        }
    }

    // GET
    protected Object get(String path) {
        String[] parts = path.split("\\.");
        String valLabel = parts[parts.length - 1];
        Map<String, Object> dataForSection = data;
        for (String part : parts) {
            if (valLabel.equals(part)) continue;

            if (dataForSection == null) {
                return null;
            }
            Object next = dataForSection.get(part);
            if (!(next instanceof Map)) {
                return null;
            }
            dataForSection = (Map<String, Object>) next;
        }
        if (dataForSection == null) {
            return null;
        }
        return dataForSection.get(valLabel);
    }
    public Object get(ValueKey key) {
        return get(key.value());
    }

    public List<String> getKeys() {
        return getKeys(false);
    }
    public List<String> getKeys(boolean deep) {
        List<String> keys = new ArrayList<>();
        collectKeys("", data, deep, keys);
        return keys;
    }
    @SuppressWarnings("unchecked")
    private void collectKeys(String prefix, Map<String, Object> map, boolean deep, List<String> keys) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fullKey = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();

            if (deep && entry.getValue() instanceof Map) {
                collectKeys(fullKey, (Map<String, Object>) entry.getValue(), deep, keys);
            } else {
                keys.add(fullKey);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Set<String> getConfigSelectionKeys(String path) {
        String[] parts = path.split("\\.");

        Map<String, Object> current = data;

        for (String part : parts) {
            if (current == null) {
                return null;
            }
            Object section = current.get(part);

            if (!(section instanceof Map<?, ?>)) {
                return Collections.emptySet();
            }

            current = (Map<String, Object>) section;
        }

        return current.keySet();
    }

    public String getString(ValueKey key) {
        return (String) get(key);
    }
    public String getString(String key) {
        return (String) get(key);
    }

    public int getInt(ValueKey key) {
        return (int) get(key);
    }
    public int getInt(String key) {
        return (int) get(key);
    }

    public boolean getBoolean(ValueKey key) {
        return (boolean) get(key);
    }
    public boolean getBoolean(String key) {
        return (boolean) get(key);
    }

    public List<?> getList(ValueKey key) {
        return (List<?>) get(key);
    }
    public List<?> getList(String key) {
        return (List<?>) get(key);
    }

    public boolean contains(ValueKey key) {
        if (key == null) {
            return false;
        }
        return get(key) != null;
    }
    public boolean contains(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return get(key) != null;
    }

    public boolean isConfigurationSection(ValueKey key) {
        Object value = get(key);
        return value instanceof Map;
    }
    public boolean isConfigurationSection(String key) {
        Object value = get(key);
        return value instanceof Map;
    }
    // END GET

    // SET
    @SuppressWarnings("unchecked")
    public void set(String path, Object value) {
        String[] parts = path.split("\\.");

        if (data == null) {
            data = new LinkedHashMap<>();
        }

        Map<String, Object> current = data;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];

            Object section = current.get(part);

            if (section instanceof Map<?, ?>) {
                current = (Map<String, Object>) section;
            } else {
                Map<String, Object> newSection = new LinkedHashMap<>();
                current.put(part, newSection);
                current = newSection;
            }
        }

        current.put(parts[parts.length - 1], value);
    }

    public void setString(String path, String value) {
        set(path, value);
    }

    public void setInt(String path, int value) {
        set(path, value);
    }

    public void setBoolean(String path, boolean value) {
        set(path, value);
    }

    public void setList(String path, List<?> value) {
        set(path, value);
    }
    // END SET

    public void save() throws IOException {
        if (file == null) {
            throw new IOException("Can't write on InputStreamReader");
        }
        try (Writer writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        }
    }

    @Override
    public void close() throws IOException {
        if (dataToRead instanceof Reader) {
            ((Reader)dataToRead).close();
        }
    }
}
