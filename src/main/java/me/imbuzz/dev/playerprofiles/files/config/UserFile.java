package me.imbuzz.dev.playerprofiles.files.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
public class UserFile {

    private FileConfiguration configuration;
    private File file;

    public UserFile(JavaPlugin javaPlugin, String fileName) {
        file = new File(javaPlugin.getDataFolder() + File.separator + "userdata" + File.separator, fileName + ".yml");
        if (!file.exists()) {
            file.mkdir();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
