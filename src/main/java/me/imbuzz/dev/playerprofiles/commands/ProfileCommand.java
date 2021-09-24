package me.imbuzz.dev.playerprofiles.commands;

import com.google.common.collect.Lists;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import me.imbuzz.dev.playerprofiles.gui.DeleteProfile;
import me.imbuzz.dev.playerprofiles.gui.ProfileManagement;
import me.imbuzz.dev.playerprofiles.objects.ProfileManager;
import me.imbuzz.dev.playerprofiles.objects.profile.DataProfile;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class ProfileCommand implements CommandExecutor, TabCompleter {

    private final PlayerProfiles playerProfiles;

    public ProfileCommand(PlayerProfiles playerProfiles) {
        this.playerProfiles = playerProfiles;

        this.playerProfiles.getCommand("profile").setExecutor(this);
        this.playerProfiles.getCommand("profile").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) return false;

        final Player player = (Player) sender;
        final ProfileManager profileManager = playerProfiles.getProfiles().get(player.getUniqueId());

        if (args.length != 2){
            if (args.length == 1){
                if (args[0].equalsIgnoreCase("reload")){
                    if (!player.hasPermission("profile.reload")){
                        player.sendMessage(playerProfiles.getLanguage().getMessage("not_permission"));
                        return false;
                    }

                    playerProfiles.getFileManager().getProfileFileGUI().reload(playerProfiles);
                    playerProfiles.getFileManager().getConfigFile().reload(playerProfiles);
                    player.sendMessage(playerProfiles.getLanguage().getMessage("reload_completed"));
                    return true;
                }
            }

            ProfileManagement.getInventory(playerProfiles, profileManager).open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")){
            if (!player.hasPermission("profile.delete")){
                player.sendMessage(playerProfiles.getLanguage().getMessage("not_permission"));
                return false;
            }

            if (profileManager.getProfile(args[1]) == null){
                player.sendMessage(playerProfiles.getLanguage().getMessage("profile-does_not_exist"));
                return false;
            }
            if (args[1].equalsIgnoreCase("default")){
                player.sendMessage(playerProfiles.getLanguage().getMessage("profile-cannot_delete_default"));
                return false;
            }

            DeleteProfile.getInventory(playerProfiles, profileManager.getProfile(args[1]), profileManager).open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("switch")){
            if (!player.hasPermission("profile.switch")){
                player.sendMessage(playerProfiles.getLanguage().getMessage("not_permission"));
                return false;
            }

            if (profileManager.getSelectedProfile().getName().equalsIgnoreCase(args[1])) {
                player.sendMessage(playerProfiles.getLanguage().getMessage("profile-in_use"));
                return false;
            }

            if (!profileManager.switchProfile(playerProfiles, args[1], player)) {
                player.sendMessage(playerProfiles.getLanguage().getMessage("profile-does_not_exist"));
                return false;
            }
            else {
                player.sendMessage(playerProfiles.getLanguage().getMessage("profile-switch", profileManager.getProfile(args[1])));
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("new")){
            if (!player.hasPermission("profile.new")){
                player.sendMessage(playerProfiles.getLanguage().getMessage("not_permission"));
                return false;
            }
            if (profileManager.getProfileMap().size() >= playerProfiles.getFileManager().getConfigFile().getConfiguration().getInt("profile.maxNumberPerPlayer")){
                player.sendMessage(playerProfiles.getLanguage().getMessage("reached_max_profile_number"));
                return false;
            }

            if (profileManager.getProfileMap().containsKey(args[1])){
                player.sendMessage(playerProfiles.getLanguage().getMessage("profile-already_exist", profileManager.getProfile(args[1])));
                return false;
            }

            profileManager.newProfile(playerProfiles, args[1], player);
            player.sendMessage(playerProfiles.getLanguage().getMessage("profile-created", profileManager.getProfile(args[1])));
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        final List<String> completer = Lists.newArrayList();
        if (sender instanceof ConsoleCommandSender) return completer;

        final Player player = (Player) sender;
        final ProfileManager profileManager = playerProfiles.getProfiles().get(player.getUniqueId());

        if (args.length == 1){
            completer.add("switch");
            completer.add("new");
            completer.add("delete");
            if (sender.hasPermission("profile.reload")) completer.add("reload");
            return completer;
        }
        if (args.length == 2){
            if (args[0].equalsIgnoreCase("switch") || args[0].equalsIgnoreCase("delete")){
                for (DataProfile profile : profileManager.getProfiles()) completer.add(profile.getName());
                return completer;
            }
        }

        return null;
    }
}
