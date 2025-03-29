package me.sajen.ncode.antylogout.listeners;

import me.sajen.ncode.antylogout.Main;
import me.sajen.ncode.antylogout.utils.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final Main plugin;
    private final CombatManager combatManager;

    public PlayerDeathListener(Main instance) {
        plugin = instance;
        combatManager = plugin.getCombatManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (combatManager.isInCombat(player)) {
            combatManager.endCombat(player);
        }
    }
}
