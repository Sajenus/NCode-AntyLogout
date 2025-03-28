package me.sajen.ncode.antylogout.utils;

import me.sajen.ncode.antylogout.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CombatManager {
    private static final ArrayList<Player> combatPlayers = new ArrayList<>();
    private final Main plugin;

    public CombatManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        combatPlayers.add(player);
    }

    public boolean isInCombat(Player player) {
        return combatPlayers.contains(player);
    }

    public void removePlayer(Player player) {
        combatPlayers.remove(player);
    }
}
