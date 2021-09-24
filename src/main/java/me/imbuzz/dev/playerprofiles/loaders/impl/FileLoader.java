package me.imbuzz.dev.playerprofiles.loaders.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import me.imbuzz.dev.playerprofiles.files.config.UserFile;
import me.imbuzz.dev.playerprofiles.loaders.BasicLoader;
import me.imbuzz.dev.playerprofiles.objects.ProfileManager;
import me.imbuzz.dev.playerprofiles.objects.profile.DataProfile;
import me.imbuzz.dev.playerprofiles.utils.BukkitSerialization;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@RequiredArgsConstructor
public class FileLoader implements BasicLoader {
    private final PlayerProfiles playerProfiles;

    @Override
    public void loadProfiles(Player player) {
        ProfileManager profileManager = new ProfileManager();
        final UserFile userFile = new UserFile(playerProfiles, player.getUniqueId().toString());

        ConfigurationSection section = userFile.getConfiguration().getConfigurationSection("profiles");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                boolean isSelected = section.getBoolean(key + ".selected");
                double lastHealth = section.getDouble(key + ".lastHealth");
                float lastXP = (float) section.getDouble(key + ".lastXP");
                int lastHunger = section.getInt(key + ".lastHunger");
                Location lastLocation = BukkitSerialization.deserializeLocation(section.getString(key + ".lastLocation"));

                ItemStack[] enderchestItems = BukkitSerialization.itemStackArrayFromBase64(section.getString(key + ".enderchestItems"));
                ItemStack[] inventoryItems = BukkitSerialization.itemStackArrayFromBase64(section.getString(key + ".inventoryItems"));
                ItemStack[] armorItems = BukkitSerialization.itemStackArrayFromBase64(section.getString(key + ".armorItems"));

                List<PotionEffect> effects = Lists.newArrayList();
                for (String effectSectionKey : section.getStringList(key + ".effects")) {
                    PotionEffect potionEffect = BukkitSerialization.deserializePotionEffect(effectSectionKey);
                    effects.add(potionEffect);
                }

                profileManager.registerProfile(new DataProfile(key, isSelected, lastHealth, lastXP, lastHunger, lastLocation, effects, enderchestItems, inventoryItems, armorItems));
            }
        }
        else {
            profileManager.getProfileMap().put("default", new DataProfile("default", player));
            profileManager.getProfileMap().get("default").setSelected(true);
        }

        playerProfiles.getProfiles().put(player.getUniqueId(), profileManager);
    }

    @Override
    public void saveProfiles(Player player) {
        final UserFile userFile = new UserFile(playerProfiles, player.getUniqueId().toString());
        ProfileManager profileManager = playerProfiles.getProfiles().get(player.getUniqueId());
        userFile.getConfiguration().set("profiles", null);

        for (DataProfile profile : profileManager.getProfiles()) {
            if (profileManager.getSelectedProfile() == profile) profile.save(playerProfiles, player);
            userFile.getConfiguration().set("profiles." + profile.getName() + ".selected", profile.isSelected());
            userFile.getConfiguration().set("profiles." + profile.getName() + ".lastHealth", profile.getLastHealth());
            userFile.getConfiguration().set("profiles." + profile.getName() + ".lastXP", profile.getLastXP());
            userFile.getConfiguration().set("profiles." + profile.getName() + ".lastHunger", profile.getLastHunger());
            userFile.getConfiguration().set("profiles." + profile.getName() + ".lastLocation", BukkitSerialization.serializeLocation(profile.getLastLocation()));
            userFile.getConfiguration().set("profiles." + profile.getName() + ".enderchestItems", BukkitSerialization.itemStackArrayToBase64(profile.getEnderchestItems()));
            userFile.getConfiguration().set("profiles." + profile.getName() + ".inventoryItems", BukkitSerialization.itemStackArrayToBase64(profile.getInventoryItems()));
            userFile.getConfiguration().set("profiles." + profile.getName() + ".armorItems", BukkitSerialization.itemStackArrayToBase64(profile.getArmorItems()));
            userFile.getConfiguration().set("profiles." +
                    profile.getName() + ".effects", BukkitSerialization.getEffectListString(profile.getPotionEffects()));
        }
        userFile.save();

        //TODO: RELOAD COMMAND, DELETE COMMAND AND DELETE GUI AND LOADERS MYSQL AND MONGODB

    }

}
