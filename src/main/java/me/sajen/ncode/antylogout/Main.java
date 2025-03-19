package me.sajen.ncode.antylogout;

import me.sajen.ncode.antylogout.listeners.EntityDamage;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        new EntityDamage(this);

    }

}
