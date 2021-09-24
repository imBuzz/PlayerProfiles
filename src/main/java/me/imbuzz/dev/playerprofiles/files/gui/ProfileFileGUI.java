package me.imbuzz.dev.playerprofiles.files.gui;

import lombok.Getter;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class ProfileFileGUI {

    private FileConfiguration configuration;
    private File file;
    private String fileName;

    public ProfileFileGUI(PlayerProfiles playerProfiles, String name) {
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
