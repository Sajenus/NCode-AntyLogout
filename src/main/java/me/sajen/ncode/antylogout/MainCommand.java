package me.sajen.ncode.antylogout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = false;
        if (sender instanceof Player) isPlayer = true;

        return true;
    }

    private void sendCommandList(CommandSender sender) {

    }
}
