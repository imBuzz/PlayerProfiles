package me.imbuzz.dev.playerprofiles.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Tools {

    public static void sendMessage(Player player, String message){
        player.sendMessage(translate(message));
    }

    public static String translate(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> translate(List<String> s){
        List<String> lists = new ArrayList<>();

        for (String s1 : s) {
            lists.add(translate(s1));
        }

        return lists;
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public static String doubleDigitNumber(double number) {
        return new DecimalFormat("#.0").format(number);
    }

}
