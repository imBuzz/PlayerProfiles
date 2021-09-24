package me.imbuzz.dev.playerprofiles;

import com.google.common.collect.Maps;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import lombok.Setter;
import me.imbuzz.dev.playerprofiles.commands.ProfileCommand;
import me.imbuzz.dev.playerprofiles.files.FileManager;
import me.imbuzz.dev.playerprofiles.files.lang.Language;
import me.imbuzz.dev.playerprofiles.listeners.EventListener;
import me.imbuzz.dev.playerprofiles.loaders.BasicLoader;
import me.imbuzz.dev.playerprofiles.loaders.impl.FileLoader;
import me.imbuzz.dev.playerprofiles.objects.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

@Getter
public final class PlayerProfiles extends JavaPlugin {

    private BasicLoader loader;
    private final Map<UUID, ProfileManager> profiles = Maps.newHashMap();

    private FileManager fileManager;
    @Setter private Language language;

    @Override
    public void onEnable() {
        fileManager = new FileManager(this);
        loader = new FileLoader(this);
        language = new Language(fileManager.getConfigFile());

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        new ProfileCommand(this);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) loader.loadProfiles(onlinePlayer);

        getLogger().info("PlayerProfiles Enabled!");
    }

    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) loader.saveProfiles(onlinePlayer);
        getLogger().info("PlayerProfiles Disabled!");
    }
}
