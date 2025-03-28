package me.sajen.ncode.antylogout;

import me.sajen.ncode.antylogout.listeners.EntityDamageListener;
import me.sajen.ncode.antylogout.utils.CombatManager;
import me.sajen.ncode.antylogout.utils.VersionChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    private CombatManager combatManager;

    @Override
    public void onEnable() {
        new VersionChecker(this).checkForUpdates();

        saveDefaultConfig();
        new EntityDamageListener(this);
        combatManager = new CombatManager(this);
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }
}