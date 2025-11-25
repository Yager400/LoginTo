package net.loginto.JSON;

import com.google.gson.*;
import java.io.*;

public class JsonMenager {
    private File file;
    private JsonObject jsonObject;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public JsonMenager(File parentFolder, String filename) {
        this.file = new File(parentFolder, filename);
        if (!file.exists()) {
            jsonObject = new JsonObject();
        } else {
            load();
        }
    }

    public boolean exists() {
        return file.exists();
    }

    public boolean createNewFile() throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file.createNewFile();
    }

    public void load() {
        if (!file.exists()) {
            jsonObject = new JsonObject();
            return;
        }
        try (Reader reader = new FileReader(file)) {
            jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject = new JsonObject();
        }
    }


    public String getString(String path) {
        JsonElement element = getElement(path);
        return element != null ? element.getAsString() : null;
    }

    public int getInt(String path) {
        JsonElement element = getElement(path);
        return element != null ? element.getAsInt() : 0;
    }

    public boolean getBoolean(String path) {
        JsonElement element = getElement(path);
        return element != null ? element.getAsBoolean() : false;
    }


    public void set(String path, Object value) {
        String[] parts = path.split("\\.");
        JsonObject current = jsonObject;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.has(part) || !current.get(part).isJsonObject()) {
                current.add(part, new JsonObject());
            }
            current = current.getAsJsonObject(part);
        }

        String key = parts[parts.length - 1];
        if (value instanceof Number) {
            current.addProperty(key, (Number) value);
        } else if (value instanceof Boolean) {
            current.addProperty(key, (Boolean) value);
        } else {
            current.addProperty(key, value.toString());
        }
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonElement getElement(String path) {
        String[] parts = path.split("\\.");
        JsonElement current = jsonObject;

        for (String part : parts) {
            if (current.isJsonObject()) {
                JsonObject obj = current.getAsJsonObject();
                if (!obj.has(part)) return null;
                current = obj.get(part);
            } else {
                return null;
            }
        }
        return current;
    }

    public void remove(String path) {
        String[] parts = path.split("\\.");
        JsonObject current = jsonObject;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.has(part) || !current.get(part).isJsonObject()) {
                return;
            }
            current = current.getAsJsonObject(part);
        }

        current.remove(parts[parts.length - 1]);
    }
}
