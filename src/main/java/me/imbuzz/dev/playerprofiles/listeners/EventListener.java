package me.imbuzz.dev.playerprofiles.listeners;

import lombok.RequiredArgsConstructor;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import me.imbuzz.dev.playerprofiles.objects.ProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class EventListener implements Listener {
    private final PlayerProfiles playerProfiles;

    @EventHandler
    public void login(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        playerProfiles.getLoader().loadProfiles(player);
        final ProfileManager profileManager = playerProfiles.getProfiles().get(player.getUniqueId());
        profileManager.getSelectedProfile().updatePlayer(playerProfiles, player);

        player.sendMessage(playerProfiles.getLanguage().getMessage("join-message", profileManager.getSelectedProfile()));
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event){
        playerProfiles.getLoader().saveProfiles(event.getPlayer());
    }


}
