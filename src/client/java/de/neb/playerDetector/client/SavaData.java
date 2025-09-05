// SavaData.java
package de.neb.playerDetector.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Speichert einfache Mod-Settings als JSON-Datei im Config-Ordner.
 */
public class SavaData {

    private JSONObject jsonObject = new JSONObject();
    private final File file;

    public SavaData() {
        // Pfad im Config-Ordner erstellen
        File configDir = FabricLoader.getInstance().getConfigDir().toFile();
        file = new File(configDir, "playerDetector/AfkPlayerDetectorSavedData.json");

        // Verzeichnis erstellen, falls nicht vorhanden
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // JSON laden, falls Datei existiert
        load();
    }

    /**
     * Speichert einen Key-Value-Pair und überschreibt den Wert, falls schon vorhanden.
     */
    public void saveDataKey(String keyName, String keyValue) {
        jsonObject.put(keyName, keyValue);
        saveAll();
    }

    /**
     * Liest einen gespeicherten Wert. Gibt null zurück, falls nicht vorhanden.
     */
    public String getDataKey(String keyName) {
        Object value = jsonObject.get(keyName);
        return value != null ? value.toString() : null;
    }

    /**
     * Löscht einen gespeicherten Key.
     */
    public void removeDataKey(String keyName) {
        jsonObject.remove(keyName);
        saveAll();
    }

    /**
     * Lädt die JSON-Datei in jsonObject.
     */
    private void load() {
        if (!file.exists()) return; // nichts zu laden
        try (FileReader reader = new FileReader(file)) {
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            Object obj = parser.parse(reader);
            if (obj instanceof JSONObject) {
                jsonObject = (JSONObject) obj;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Speichert das jsonObject in die Datei.
     */
    private void saveAll() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonObject.toJSONString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
