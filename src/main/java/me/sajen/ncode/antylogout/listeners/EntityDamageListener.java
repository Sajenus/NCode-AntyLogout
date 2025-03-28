package me.sajen.ncode.antylogout.listeners;

import me.sajen.ncode.antylogout.utils.CombatManager;
import me.sajen.ncode.antylogout.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;

public class EntityDamageListener implements Listener {

    private final Map<Player, Long> endTimes = new HashMap<>();
    private final Main plugin;
    private final CombatManager combatManager;

    public EntityDamageListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getHealth() - event.getFinalDamage() <= 0) return;

        long combatTime;
        DamageCause cause = event.getCause();

        if (cause.equals(DamageCause.ENTITY_ATTACK)
        || cause.equals(DamageCause.ENTITY_SWEEP_ATTACK)
        || cause.equals(DamageCause.ENTITY_EXPLOSION)
        || cause.equals(DamageCause.PROJECTILE)
        || cause.equals(DamageCause.MAGIC)) {
            EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent) event;

            // PLAYER
            if (newEvent.getDamager() instanceof Player) {
                combatTime = playerTime();
            }

            // CREEPER
            else if (cause.equals(DamageCause.ENTITY_EXPLOSION) && newEvent.getDamager() instanceof Creeper) {
                combatTime = mobTime();
            }

            // POTION EFFECT
            else if (cause.equals(DamageCause.MAGIC)) {
                combatTime = mobTime();
            }

            // PROJECTILE
            else if (cause.equals(DamageCause.PROJECTILE)) {
                if (newEvent.getDamager() instanceof Fireball fireball) {
                    if (fireball.getShooter() instanceof Player) combatTime = playerTime();
                    else if (fireball.getShooter() instanceof LivingEntity) combatTime = mobTime();
                    else combatTime = otherTime();
                }
                else if (newEvent.getDamager() instanceof Arrow arrow) {
                    if (arrow.getShooter() instanceof Player) combatTime = playerTime();
                    else if (arrow.getShooter() instanceof LivingEntity) combatTime = mobTime();
                    else combatTime = otherTime();
                }
                else if (newEvent.getDamager() instanceof SpectralArrow spectralArrow) {
                    if (spectralArrow.getShooter() instanceof Player) combatTime = playerTime();
                    else if (spectralArrow.getShooter() instanceof LivingEntity) combatTime = mobTime();
                    else combatTime = otherTime();
                }
                else if (newEvent.getDamager() instanceof ShulkerBullet shulkerBullet) {
                    combatTime = mobTime();
                }
                else if (newEvent.getDamager() instanceof EnderPearl) {
                    combatTime = playerTime();
                }
                else if (newEvent.getDamager() instanceof Trident trident) {
                    if (trident.getShooter() instanceof Player) combatTime = playerTime();
                    else combatTime = mobTime();
                }
                else {
                    player.sendMessage(newEvent.getDamager().toString());
                    combatTime = otherTime();
                }
            }

            // MOB
            else if (newEvent.getDamager() instanceof LivingEntity){
                combatTime = mobTime();
            }

            // OTHER
            else {
                combatTime = otherTime();
            }
        }
        else {
            combatTime = otherTime();
        }

        long endTime = System.currentTimeMillis() + combatTime;
        endTimes.put(player, endTime);

        // Jeśli gracz ma już antylogout to przedłużamy
        if (combatManager.isInCombat(player)) return;

        // Gracz dostaje dopiero antylogout
        sendActionBar(player, Color(plugin.getConfig().getString("messages.actionbar-1")));
        combatManager.addPlayer(player);
        taskTimer(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (combatManager.isInCombat(player)) {
            endTimes.put(player, -1L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(combatManager.isInCombat(player)) {
            player.setHealth(0);
            endTimes.put(player, -1L);
            Bukkit.broadcastMessage(Color(plugin.getConfig().getString("messages.logout").replace("%player%", player.getName())));
        }
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("ncode.antylogout.commands")) return; // Gracz może używać komend podczas antylogoutu
        if (!combatManager.isInCombat(player)) return; // Gracz nie ma antylogoutu

        String[] command = event.getMessage().split(" ");

        // Dla wszystkich dozwolonych komend podczas antylogoutu
        for (String label : plugin.getConfig().getStringList("enabled-commands")) {
            // Komenda nie jest dozwolona
            if (!command[0].substring(1).equalsIgnoreCase(label)) {
                player.sendMessage(Color(plugin.getConfig().getString("messages.command-in-combat")));
                event.setCancelled(true);
            }
            return;
        }
    }

    private void taskTimer(Player player) {
        String text = plugin.getConfig().getString("messages.actionbar-2");
        int textLength = text.length();
        int[] textTicks = new int[textLength];

        ChatColor[] gradientColors = new ChatColor[]{
                ChatColor.of("#fb0000"),
                ChatColor.of("#fb0800"),
                ChatColor.of("#fb1100"),
                ChatColor.of("#fb1900"),
                ChatColor.of("#fb2200"),
                ChatColor.of("#fb2a00"),
                ChatColor.of("#fb3200"),
                ChatColor.of("#fb3b00"),
                ChatColor.of("#fc4300"),
                ChatColor.of("#fc4b00"),
                ChatColor.of("#fc5400"),
                ChatColor.of("#fc5c00"),
                ChatColor.of("#fc6500"),
                ChatColor.of("#fc6d00"),
                ChatColor.of("#fc7500"),
                ChatColor.of("#fc7e00"),
                ChatColor.of("#fc8600"),
                ChatColor.of("#fc8e00"),
                ChatColor.of("#fc9700"),
                ChatColor.of("#fc9f00"),
                ChatColor.of("#fca800"),
                ChatColor.of("#fcb000"),
                ChatColor.of("#fdb800"),
                ChatColor.of("#fdc100"),
                ChatColor.of("#fdc900"),
                ChatColor.of("#fdd100"),
                ChatColor.of("#fdda00"),
                ChatColor.of("#fde200"),
                ChatColor.of("#fdeb00"),
                ChatColor.of("#fdf300"),

                ChatColor.of("#fdeb00"),
                ChatColor.of("#fde200"),
                ChatColor.of("#fdda00"),
                ChatColor.of("#fdd100"),
                ChatColor.of("#fdc900"),
                ChatColor.of("#fdc100"),
                ChatColor.of("#fdb800"),
                ChatColor.of("#fcb000"),
                ChatColor.of("#fca800"),
                ChatColor.of("#fc9f00"),
                ChatColor.of("#fc9700"),
                ChatColor.of("#fc8e00"),
                ChatColor.of("#fc8600"),
                ChatColor.of("#fc7e00"),
                ChatColor.of("#fc7500"),
                ChatColor.of("#fc6d00"),
                ChatColor.of("#fc6500"),
                ChatColor.of("#fc5c00"),
                ChatColor.of("#fc5400"),
                ChatColor.of("#fc4b00"),
                ChatColor.of("#fc4300"),
                ChatColor.of("#fb3b00"),
                ChatColor.of("#fb3200"),
                ChatColor.of("#fb2a00"),
                ChatColor.of("#fb2200"),
                ChatColor.of("#fb1900"),
                ChatColor.of("#fb1100"),
                ChatColor.of("#fb0800")
        };

        BukkitRunnable gradientRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!combatManager.isInCombat(player)) return;

                if (System.currentTimeMillis() >= endTimes.get(player)) {
                    sendActionBar(player, Color(plugin.getConfig().getString("messages.actionbar-3")));
                    combatManager.removePlayer(player);
                    endTimes.remove(player);
                    cancel();
                    return;
                }

                StringBuilder animatedText = new StringBuilder();
                for (int i = 0; i < textLength; i++) {
                    ChatColor color = gradientColors[(textTicks[i] + i) % gradientColors.length];
                    animatedText.append(color).append(ChatColor.BOLD).append(text.charAt(i));
                    textTicks[i] = (textTicks[i] + 1) % gradientColors.length;
                }
                sendActionBar(player, animatedText.toString());
            }
        };
        gradientRunnable.runTaskTimer(plugin, plugin.getConfig().getLong("messages.actionbar-1-delay"), 1L);
    }

    private long playerTime() {
        return plugin.getConfig().getInt("Player") * 1000L;
    }
    private long mobTime() {
        return plugin.getConfig().getInt("Mob") * 1000L;
    }
    private long otherTime() {
        return plugin.getConfig().getInt("Other") * 1000L;
    }

    private String Color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

}
