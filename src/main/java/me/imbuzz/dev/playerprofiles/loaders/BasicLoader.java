package me.imbuzz.dev.playerprofiles.loaders;

import org.bukkit.entity.Player;

public interface BasicLoader {

    void loadProfiles(Player player);
    void saveProfiles(Player player);


}
