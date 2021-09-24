package me.imbuzz.dev.playerprofiles.files;

import lombok.Getter;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import me.imbuzz.dev.playerprofiles.files.config.ConfigFile;
import me.imbuzz.dev.playerprofiles.files.gui.ProfileFileGUI;

@Getter
public class FileManager {

    private final PlayerProfiles profiles;
    private final ConfigFile configFile;
    private final ProfileFileGUI profileFileGUI;

    public FileManager(PlayerProfiles playerProfiles) {
        profiles = playerProfiles;
        configFile = new ConfigFile(profiles, "config.yml");
        profileFileGUI = new ProfileFileGUI(profiles, "profile.yml");
    }
}
