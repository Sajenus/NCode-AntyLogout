package me.sajen.ncode.antylogout.utils;

import me.sajen.ncode.antylogout.Main;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

public class VersionChecker {
    private final Main plugin;

    public VersionChecker(Main plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        (new BukkitRunnable() {
            public void run() {
                try {
                    URL url = new URL("https://raw.githubusercontent.com/Sajenus/NCode-AntyLogout/refs/heads/master/pom.xml");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder xmlContent = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        xmlContent.append(line);
                    }
                    reader.close();

                    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xmlContent.toString().getBytes()));

                    String currentVersion = plugin.getDescription().getVersion();
                    String latestVersion = document.getElementsByTagName("version").item(0).getTextContent();
                    Level level = getLoggerLevel(currentVersion, latestVersion);

                    if (level != null) {
                        newVersionInfo(level, currentVersion, latestVersion);
                        return;
                    }

                    plugin.getLogger().info("Twoja wersja (" + currentVersion + ") jest aktualna!");

                } catch (Exception e) {
                    plugin.getLogger().warning("[" + plugin.getName() + "] Nie udalo sie sprawdzic wersji: " + e.getMessage());
                }
            }
        }).runTaskAsynchronously(plugin);
    }
    private Level getLoggerLevel(String currentVersion, String latestVersion) {
        String[] currentVersionSplit = currentVersion.split("\\.");
        String[] latestVersionSplit = latestVersion.split("\\.");

        Level level = null;

        if (!latestVersionSplit[0].equals(currentVersionSplit[0])) {
            level = Level.SEVERE;
        } else {
            if (!latestVersionSplit[1].equals(currentVersionSplit[1])) {
                level = Level.WARNING;
            } else {
                if (!latestVersionSplit[2].equals(currentVersionSplit[2])) {
                    level = Level.INFO;
                }
            }
        }
        return level;
    }
    private void newVersionInfo(Level level, String currentVersion, String latestVersion) {
        String str1 = "[" + plugin.getName() + "] Nowa wersja pluginu jest dostepna! Aktualna wersja: " + currentVersion + ", Najnowsza wersja: " + latestVersion;
        String str2 = "[" + plugin.getName() + "] Zglos sie na ticket na oficjalnym Discordzie NCode: discord.gg/ahFdHnRy4H";

        plugin.getLogger().log(level, str1);
        plugin.getLogger().log(level, str2);
    }
}
