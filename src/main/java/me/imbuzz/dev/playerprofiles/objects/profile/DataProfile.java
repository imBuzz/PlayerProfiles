package me.imbuzz.dev.playerprofiles.objects.profile;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import me.imbuzz.dev.playerprofiles.files.config.ConfigFile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Data @AllArgsConstructor
public class DataProfile {

    private String name;
    private boolean selected;
    private double lastHealth;
    private float lastXP;
    private int lastHunger;
    private Location lastLocation;

    private List<PotionEffect> potionEffects;

    private ItemStack[] enderchestItems;
    private ItemStack[] inventoryItems;
    private ItemStack[] armorItems;

    public DataProfile(String key, Player player){
        name = key;
        selected = false;

        potionEffects = Lists.newArrayList();

        enderchestItems = new ItemStack[player.getEnderChest().getContents().length];
        inventoryItems = new ItemStack[player.getInventory().getContents().length];
        armorItems = new ItemStack[player.getInventory().getArmorContents().length];

        lastHealth = 20;
        lastHunger = 20;
        lastXP = 0;
        lastLocation = player.getLocation();
    }

    public void save(PlayerProfiles playerProfiles, Player player){
        ConfigFile configFile = playerProfiles.getFileManager().getConfigFile();

        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncHealth")) lastHealth = player.getHealth();
        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncHunger")) lastHunger = player.getFoodLevel();
        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncXP")) lastXP = player.getExp();
        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncLocation")) lastLocation = player.getLocation();

        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncEffects")){
            potionEffects.clear();
            potionEffects.addAll(player.getActivePotionEffects());
        }

        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncInventory")){
            inventoryItems = player.getInventory().getContents().clone();
            armorItems = player.getInventory().getArmorContents().clone();
        }

        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncEnderchest")) enderchestItems = player.getEnderChest().getContents().clone();
    }

    public void updatePlayer(PlayerProfiles playerProfiles, Player player){
        ConfigFile configFile = playerProfiles.getFileManager().getConfigFile();

        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncHealth")) player.setHealth(lastHealth);
        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncHunger")) player.setFoodLevel(lastHunger);
        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncXP")) player.setExp(lastXP);

        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncEffects")){
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) player.removePotionEffect(activePotionEffect.getType());
            player.addPotionEffects(potionEffects);
        }
        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncInventory")){
            player.getInventory().clear();
            player.getInventory().setContents(inventoryItems);
            player.getInventory().setArmorContents(armorItems);
            player.updateInventory();
        }

        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncEnderchest")) player.getEnderChest().setContents(enderchestItems);
        if (configFile.getConfiguration().getBoolean("profile.syncOptions.syncLocation")) player.teleport(lastLocation);
    }





}