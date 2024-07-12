package me.niko302.hologramchat.commands;

import me.niko302.hologramchat.HologramChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

    private final HologramChat plugin;

    public Commands(HologramChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hologramchatreload")) {
            if (sender.hasPermission("hologramchat.reload")) {
                // Reload plugin configuration
                plugin.reloadConfig();
                // Reload ConfigManager settings
                plugin.getConfigManager().reloadConfigurations();
                sender.sendMessage(plugin.color("&aHologramChat configurations reloaded."));
            } else {
                sender.sendMessage(plugin.color("&cYou do not have permission to perform this command."));
            }
            return true;
        }
        return false;
    }
}