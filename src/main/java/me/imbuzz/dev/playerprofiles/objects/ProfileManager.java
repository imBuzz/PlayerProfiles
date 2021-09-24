package me.imbuzz.dev.playerprofiles.objects;

import lombok.Getter;
import me.imbuzz.dev.playerprofiles.PlayerProfiles;
import me.imbuzz.dev.playerprofiles.objects.profile.DataProfile;
import me.imbuzz.dev.playerprofiles.objects.structures.StringMap;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ProfileManager {

    @Getter private final StringMap<DataProfile> profileMap = new StringMap<>();

    public Collection<DataProfile> getProfiles() {
        return profileMap.values();
    }

    public void newProfile(PlayerProfiles playerProfiles, String key, Player player) {
        profileMap.put(key, new DataProfile(key, player));
        switchProfile(playerProfiles, key, player);
    }

    public void registerProfile(DataProfile profile){
        profileMap.put(profile.getName(), profile);
    }

    public DataProfile getSelectedProfile(){
        for (DataProfile profile : getProfiles()) {
            if (profile.isSelected()) return profile;
        }
        return null;
    }

    public DataProfile getProfile(String key){
        return profileMap.get(key);
    }

    public boolean switchProfile(PlayerProfiles playerProfiles, String key, Player player){
        if (!profileMap.containsKey(key)) return false;

        DataProfile oldProfile = getSelectedProfile();
        DataProfile profile = profileMap.get(key);

        oldProfile.save(playerProfiles, player);
        oldProfile.setSelected(false);

        profile.setSelected(true);
        profile.updatePlayer(playerProfiles, player);

        return true;
    }

}
