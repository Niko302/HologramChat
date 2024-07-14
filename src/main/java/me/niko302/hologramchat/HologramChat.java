package me.niko302.hologramchat;

import lombok.Getter;
import me.niko302.hologramchat.commands.Commands;
import me.niko302.hologramchat.config.ConfigManager;
import me.niko302.hologramchat.listeners.ChatListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public final class HologramChat extends JavaPlugin {

    private ConfigManager configManager;
    private ChatListener chatListener;

    @Override
    public void onEnable() {
        // Initialize ConfigManager
        configManager = new ConfigManager(this);

        // Register event listeners
        chatListener = new ChatListener(this);
        getServer().getPluginManager().registerEvents(chatListener, this);

        // Register commands
        this.getCommand("hologramchatreload").setExecutor(new Commands(this));

        new Metrics(this, 22619);

        new com.jeff_media.updatechecker.UpdateChecker(this, com.jeff_media.updatechecker.UpdateCheckSource.SPIGOT, "00000")
                .setNotifyRequesters(false)
                .setNotifyOpsOnJoin(false)
                .setUserAgent(com.jeff_media.updatechecker.UserAgentBuilder.getDefaultUserAgent())
                .checkEveryXHours(12)
                .onSuccess((commandSenders, latestVersion) -> {
                    String messagePrefix = "&8[&6HologramChat&8] ";
                    String currentVersion = getDescription().getVersion();

                    if (currentVersion.equalsIgnoreCase(latestVersion)) {
                        String updateMessage = color(messagePrefix + "&aYou are using the latest version of HologramChat!");
                        Bukkit.getConsoleSender().sendMessage(updateMessage);
                        Bukkit.getOnlinePlayers().stream().filter(op -> op.isOp()).forEach(player -> player.sendMessage(updateMessage));
                        return;
                    }

                    List<String> updateMessages = List.of(
                            color(messagePrefix + "&cYour version of HologramChat is outdated!"),
                            color(String.format(messagePrefix + "&cYou are using %s, latest is %s!", currentVersion, latestVersion)),
                            color(messagePrefix + "&cDownload latest here:"),
                            color("&6https://www.spigotmc.org/resources/hologramchat/")
                    );

                    Bukkit.getConsoleSender().sendMessage(updateMessages.toArray(new String[0]));
                    Bukkit.getOnlinePlayers().stream().filter(op -> op.isOp()).forEach(player -> player.sendMessage(updateMessages.toArray(new String[0])));
                })
                .onFail((commandSenders, e) -> {}).checkNow();
    }

    @Override
    public void onDisable() {
        // Clear all ArmorStands when the server stops
        if (chatListener != null) {
            chatListener.clearAllArmorStands();
        }
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}