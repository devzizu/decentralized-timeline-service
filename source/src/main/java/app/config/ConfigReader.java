package app.config;

import java.io.File;

import com.moandjiezana.toml.Toml;

public class ConfigReader {
    
    private static Toml tomlObj;

    public static void read(String filename) {
        try {

            File configFile = new File(filename);
            tomlObj = new Toml().read(configFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getLong(String key, String value) {

        return tomlObj.getTable(key).getLong(value);
    }

    public static String getString(String key, String value) {

        return tomlObj.getTable(key).getString(value);
    }
}
