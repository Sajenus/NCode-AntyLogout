package me.sajen.ncode.antylogout.utils;

import me.sajen.ncode.antylogout.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CombatManager {
    private final Main plugin;
    private static final ArrayList<Player> combatPlayers = new ArrayList<>();
    private final Map<Player, Long> endTimes = new HashMap<>();

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
        endTimes.remove(player);
    }

    public void endCombat(Player player) {
        endTimes.put(player, -1L);
    }

    public Long getEndTime(Player player) {
        return endTimes.get(player);
    }

    public void addEndTime(Player player, Long endTime) {
        endTimes.put(player, endTime);
    }
}
