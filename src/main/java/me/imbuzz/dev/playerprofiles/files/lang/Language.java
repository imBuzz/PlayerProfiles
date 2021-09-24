package me.imbuzz.dev.playerprofiles.files.lang;

import lombok.AllArgsConstructor;
import me.imbuzz.dev.playerprofiles.files.config.ConfigFile;
import me.imbuzz.dev.playerprofiles.objects.profile.DataProfile;
import me.imbuzz.dev.playerprofiles.utils.Tools;

@AllArgsConstructor
public class Language {

    private final ConfigFile configFile;

    public String getMessage(String path, DataProfile profile){
        return Tools.translate(configFile.getConfiguration().getString("lang." + path).replaceAll("%profile%", profile.getName()));
    }

    public String getMessage(String path){
        return Tools.translate(configFile.getConfiguration().getString("lang." + path));
    }


}
