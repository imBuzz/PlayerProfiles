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

public class ProfileManagement implements InventoryProvider {

    private static SmartInventory smartInventory;
    private final PlayerProfiles playerProfiles;
    private final ProfileManager profileManager;
    private final ProfileFileGUI profileFileGUI;

    public ProfileManagement(PlayerProfiles pl, ProfileManager pm) {
        playerProfiles = pl;
        profileManager = pm;
        profileFileGUI = playerProfiles.getFileManager().getProfileFileGUI();
    }

    public static SmartInventory getInventory(PlayerProfiles playerProfiles, ProfileManager pm) {
        smartInventory = SmartInventory.builder()
                .id("Members")
                .provider(new ProfileManagement(playerProfiles, pm))
                .size(3, 9)
                .title(Tools.translate(playerProfiles.getFileManager().getProfileFileGUI().getConfiguration().getString("GUI.title")))
                .build();
        return smartInventory;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        ArrayList<ClickableItem> currentItems = createProfiles(player);
        ClickableItem[] items = new ClickableItem[currentItems.size()];

        for (int i = 0; i < items.length; i++) {
            items[i] = currentItems.get(i);
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(9);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        if (currentItems.size() > 27) {
            contents.set(2, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).setName(ChatColor.GRAY + "Indietro").build(), e -> {
                smartInventory.open(player, pagination.previous().getPage());
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);

            }));
            contents.set(2, 5, ClickableItem.of(new ItemBuilder(Material.ARROW).setName(ChatColor.GRAY + "Avanti").build(), e -> {
                smartInventory.open(player, pagination.next().getPage());
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);

            }));
        }
        createCloseItem(player, contents);
    }

    private ArrayList<ClickableItem> createProfiles(Player player) {
        ArrayList<ClickableItem> items = new ArrayList<>();

        Material material = Material.valueOf(profileFileGUI.getConfiguration().getString("GUI.items.basicProfile.type").toUpperCase(Locale.ROOT));
        short damage = (short) profileFileGUI.getConfiguration().getInt("GUI.items.basicProfile.damage");

        for (DataProfile profile : profileManager.getProfiles()) {
            ItemBuilder itemBuilder = new ItemBuilder(material, damage);

            if (profile.isSelected()) itemBuilder.addEnchant(Enchantment.DAMAGE_UNDEAD, 1);
            itemBuilder.setFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);

            String name = profileFileGUI.getConfiguration().getString("GUI.items.basicProfile.name");
            List<String> lore = new ArrayList<>();
            for (String s : profileFileGUI.getConfiguration().getStringList("GUI.items.basicProfile.lore")) lore.add(addPlaceholder(profile, s));
            itemBuilder.setName(addPlaceholder(profile, name));
            itemBuilder.setLore(lore);

            items.add(ClickableItem.of(itemBuilder.build(), event -> {
                if (event.getClick().isRightClick()){
                    if (!player.hasPermission("profile.delete")){
                        player.sendMessage(playerProfiles.getLanguage().getMessage("not_permission"));
                        return;
                    }
                    if (profile.getName().equalsIgnoreCase("default")){
                        player.sendMessage(playerProfiles.getLanguage().getMessage("profile-cannot_delete_default"));
                        return;
                    }

                    DeleteProfile.getInventory(playerProfiles, profile, profileManager).open(player);
                }
                else {
                    if (profileManager.getSelectedProfile().getName().equalsIgnoreCase(profile.getName())) {
                        player.sendMessage(playerProfiles.getLanguage().getMessage("profile-in_use"));
                        return;
                    }
                    if (!profileManager.switchProfile(playerProfiles, profile.getName(), player)) {
                        player.sendMessage(playerProfiles.getLanguage().getMessage("profile-does_not_exist", profileManager.getProfile(profile.getName())));
                    }
                    else {
                        player.sendMessage(playerProfiles.getLanguage().getMessage("profile-switch", profileManager.getProfile(profile.getName())));
                    }
                }
            }));
        }
        return items;
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

    public void createCloseItem(Player player, InventoryContents contents) {
        Material material = Material.valueOf(profileFileGUI.getConfiguration().getString("GUI.items.closeProfile.type").toUpperCase(Locale.ROOT));
        short damage = (short) profileFileGUI.getConfiguration().getInt("GUI.items.closeProfile.damage");
        String name = profileFileGUI.getConfiguration().getString("GUI.items.closeProfile.name");
        List<String> lore = profileFileGUI.getConfiguration().getStringList("GUI.items.closeProfile.lore");

        ItemBuilder itemBuilder = new ItemBuilder(material, damage);
        itemBuilder.setName(name);
        itemBuilder.setLore(lore);
        contents.set(2, 4, ClickableItem.of(itemBuilder.build(), e -> {
            player.closeInventory();
        }));
    }

}
