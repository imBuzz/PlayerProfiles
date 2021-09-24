package me.imbuzz.dev.playerprofiles.files.config;

import lombok.Getter;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
public class ConfigFile {

    private FileConfiguration configuration;
    private File file;
    private String fileName;

    public ConfigFile(PlayerProfiles playerProfiles, String name) {
        fileName = name;
        file = new File(playerProfiles.getDataFolder(), fileName);
        if (!file.exists()) {
            playerProfiles.saveResource(fileName, true);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void reload(PlayerProfiles playerProfiles){
        file = new File(playerProfiles.getDataFolder(), fileName);
        if (!file.exists()) {
            playerProfiles.saveResource(fileName, true);
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
