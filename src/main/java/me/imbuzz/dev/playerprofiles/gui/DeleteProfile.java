package me.imbuzz.dev.playerprofiles.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import me.imbuzz.dev.playerprofiles.files.gui.ProfileFileGUI;
import me.imbuzz.dev.playerprofiles.objects.ProfileManager;
import me.imbuzz.dev.playerprofiles.objects.profile.DataProfile;
import me.imbuzz.dev.playerprofiles.utils.ItemBuilder;
import me.imbuzz.dev.playerprofiles.utils.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeleteProfile implements InventoryProvider {

    private static SmartInventory smartInventory;
    private final PlayerProfiles playerProfiles;
    private final ProfileManager profileManager;
    private final DataProfile dataProfile;
    private final ProfileFileGUI profileFileGUI;

    public DeleteProfile(PlayerProfiles pl, DataProfile dt, ProfileManager pm) {
        playerProfiles = pl;
        profileManager = pm;
        dataProfile = dt;
        profileFileGUI = playerProfiles.getFileManager().getProfileFileGUI();
    }

    public static SmartInventory getInventory(PlayerProfiles playerProfiles, DataProfile dataProfile, ProfileManager pm) {
        smartInventory = SmartInventory.builder()
                .id("Members")
                .provider(new DeleteProfile(playerProfiles, dataProfile, pm))
                .size(3, 9)
                .title(Tools.translate(playerProfiles.getFileManager().getProfileFileGUI().getConfiguration().getString("DELETEGUI.title")))
                .build();
        return smartInventory;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        createConfirm(player, contents);
        createDelete(player, contents);
    }

    private void createConfirm(Player player, InventoryContents contents){
        Material material = Material.valueOf(profileFileGUI.getConfiguration().getString("DELETEGUI.items.deleteConfirm.type").toUpperCase(Locale.ROOT));
        short damage = (short) profileFileGUI.getConfiguration().getInt("DELETEGUI.items.deleteConfirm.damage");
        ItemBuilder itemBuilder = new ItemBuilder(material, damage);

        String name = profileFileGUI.getConfiguration().getString("DELETEGUI.items.deleteConfirm.name");
        List<String> lore = new ArrayList<>();
        for (String s : profileFileGUI.getConfiguration().getStringList("DELETEGUI.items.deleteConfirm.lore")) lore.add(addPlaceholder(dataProfile, s));
        itemBuilder.setName(addPlaceholder(dataProfile, name));
        itemBuilder.setLore(lore);

        contents.set(1, 2, ClickableItem.of(itemBuilder.build(), event -> {
            if (profileManager.getSelectedProfile() == dataProfile){
                profileManager.switchProfile(playerProfiles, "default", player);
            }
            profileManager.getProfileMap().remove(dataProfile.getName());
            player.sendMessage(playerProfiles.getLanguage().getMessage("profile-deleted", dataProfile));
            player.closeInventory();
        }));
    }

    private void createDelete(Player player, InventoryContents contents){
        Material material = Material.valueOf(profileFileGUI.getConfiguration().getString("DELETEGUI.items.deleteCancel.type").toUpperCase(Locale.ROOT));
        short damage = (short) profileFileGUI.getConfiguration().getInt("DELETEGUI.items.deleteCancel.damage");
        ItemBuilder itemBuilder = new ItemBuilder(material, damage);

        String name = profileFileGUI.getConfiguration().getString("DELETEGUI.items.deleteCancel.name");
        List<String> lore = new ArrayList<>();
        for (String s : profileFileGUI.getConfiguration().getStringList("DELETEGUI.items.deleteCancel.lore")) lore.add(addPlaceholder(dataProfile, s));
        itemBuilder.setName(addPlaceholder(dataProfile, name));
        itemBuilder.setLore(lore);

        contents.set(1, 5, ClickableItem.of(itemBuilder.build(), event -> {
            player.closeInventory();
        }));
    }



    public String addPlaceholder(DataProfile dataProfile, String message){
        return message
                .replaceAll("%profile_name%", Tools.toTitleCase(dataProfile.getName()))
                .replaceAll("%world%", dataProfile.getLastLocation().getWorld().getName())
                .replaceAll("%status%", dataProfile.isSelected() ? "&aSelected" : "&cNot Selected")
                .replaceAll("%loc_x%", Tools.doubleDigitNumber(dataProfile.getLastLocation().getX()))
                .replaceAll("%loc_y%", Tools.doubleDigitNumber(dataProfile.getLastLocation().getY()))
                .replaceAll("%loc_z%", Tools.doubleDigitNumber(dataProfile.getLastLocation().getZ()));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);
        if (state % (20 * 5) != 0) return;
        init(player, contents);
    }
}
