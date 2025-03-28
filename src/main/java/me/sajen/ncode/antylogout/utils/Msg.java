package me.sajen.ncode.antylogout.utils;

import org.bukkit.ChatColor;
import java.util.List;

public class Msg {
    public String formatLegacy(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> formatLegacyList(List<String> messages) {
        return messages.stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toList();
    }
}