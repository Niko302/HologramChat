package me.niko302.hologramchat.config;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.niko302.hologramchat.HologramChat;
import org.bukkit.configuration.file.FileConfiguration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ConfigManager {

    private final HologramChat plugin;
    private FileConfiguration config;
    private final Map<String, MessageGroup> groups = new HashMap<>();
    private double yOffset;
    private int displayDuration;
    private int lineLength;
    private int increaseDisplayDurationPerLine;
    private int radius;
    private List<String> disabledWorlds;
    private boolean seeOwnMessageHologram;
    private int textFollowUpdateInterval;

    // Regex to extract and process hex color codes
    private final Pattern hexColorExtractor = Pattern.compile("#([A-Fa-f0-9]{6})");

    public ConfigManager(HologramChat plugin) {
        this.plugin = plugin;
        reloadConfigurations();
    }

    public void reloadConfigurations() {
        // Reload plugin's config.yml
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        // Reload other configurations managed by ConfigManager
        this.yOffset = config.getDouble("yOffset", 1.0);
        this.displayDuration = config.getInt("displayDuration", 3);
        this.lineLength = config.getInt("lineLength", 30);
        this.increaseDisplayDurationPerLine = config.getInt("increaseDisplayDurationPerLine", 1);
        this.radius = config.getInt("radius", 50);
        this.disabledWorlds = config.getStringList("disabledWorlds");
        this.seeOwnMessageHologram = config.getBoolean("seeOwnMessageHologram", true);
        this.textFollowUpdateInterval = config.getInt("textFollowUpdateInterval", 5);
        loadMessageGroups();
    }

    public void loadMessageGroups() {
        groups.clear();
        if (config.isConfigurationSection("groups")) {
            config.getConfigurationSection("groups").getKeys(false).forEach(key -> {
                String path = "groups." + key + ".";
                groups.put(key, new MessageGroup(
                        color(config.getString(path + "messageFormat")),
                        config.getString(path + "permission"),
                        config.getInt(path + "weight", 0)
                ));
            });
        }
    }

    public static class MessageGroup {
        private final String messageFormat;
        private final String permission;
        private final int weight;

        public MessageGroup(String messageFormat, String permission, int weight) {
            this.messageFormat = messageFormat;
            this.permission = permission != null ? permission : "hologramchat.default";
            this.weight = weight;
        }

        public String getMessageFormat() {
            return messageFormat;
        }

        public String getPermission() {
            return permission;
        }

        public int getWeight() {
            return weight;
        }
    }

    public String color(String message) {
        if (message == null) return null;
        String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);
        Matcher matcher = hexColorExtractor.matcher(coloredMessage);
        while (matcher.find()) {
            String hexColor = matcher.group();
            coloredMessage = coloredMessage.replace(hexColor, ChatColor.of(hexColor).toString());
        }
        return coloredMessage;
    }

    public String applyPlaceholders(Player player, String message) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public Pattern getHexColorExtractor() {
        return hexColorExtractor;
    }

    public Map<String, MessageGroup> getGroups() {
        return groups;
    }

    public double getYOffset() {
        return yOffset;
    }

    public int getDisplayDuration() {
        return displayDuration;
    }

    public int getLineLength() {
        return lineLength;
    }

    public int getIncreaseDisplayDurationPerLine() {
        return increaseDisplayDurationPerLine;
    }

    public int getRadius() {
        return radius;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public boolean isSeeOwnMessageHologram() {
        return seeOwnMessageHologram;
    }

    public int getTextFollowUpdateInterval() {
        return textFollowUpdateInterval;
    }
}