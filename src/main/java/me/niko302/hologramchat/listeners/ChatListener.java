package me.niko302.hologramchat.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import me.niko302.hologramchat.HologramChat;
import me.niko302.hologramchat.config.ConfigManager.MessageGroup;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatListener implements Listener {

    private final HologramChat plugin;
    private final HashMap<UUID, List<ArmorStand>> playerMessages = new HashMap<>();
    private final String armorStandMetadataKey = "hologramBubble";
    private final ProtocolManager protocolManager;

    public ChatListener(HologramChat plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            plugin.getLogger().info("Chat event cancelled for player: " + event.getPlayer().getName());
            return;
        }

        Player player = event.getPlayer();
        displayHologramMessage(player, event.getMessage());
    }

    public void displayPrivateMessageHologram(Player sender, Player recipient, String message) {
        // Display message to the sender if configured
        if (plugin.getConfigManager().isSeeOwnMessageHologram()) {
            displayHologramMessage(sender, message);
        }

        // Hide holograms from other players, but make sure it's visible to both the sender and the recipient
        List<ArmorStand> armorStands = playerMessages.get(sender.getUniqueId());
        if (armorStands != null) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.equals(sender) && !onlinePlayer.equals(recipient)) {
                    hideHologramFromPlayer(onlinePlayer, armorStands);
                }
            }
        }
    }

    public void displayHologramMessage(Player player, String message) {
        World world = player.getWorld();

        // Check if the world is disabled
        if (plugin.getConfigManager().getDisabledWorlds().contains(world.getName())) {
            plugin.getLogger().info("World " + world.getName() + " is disabled for hologram chat.");
            return;
        }

        // Check if the player is in vanish, invisible, or spectator mode
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) || player.getGameMode() == GameMode.SPECTATOR) {
            plugin.getLogger().info("Player " + player.getName() + " is in vanish, invisible, or spectator mode.");
            return;
        }

        // Check if the player has any of the required permissions
        if (!hasRequiredPermission(player)) {
            plugin.getLogger().info("Player " + player.getName() + " does not have the required permission.");
            return;
        }

        UUID playerId = player.getUniqueId();
        String formattedMessage = formatMessage(player, message);
        String initialColorCode = getInitialColorCode(formattedMessage);
        List<String> messageLines = splitMessageIntoLines(formattedMessage, initialColorCode);

        List<ArmorStand> armorStands = playerMessages.get(playerId);
        if (armorStands != null) {
            plugin.getLogger().info("Removing existing armor stands for player: " + player.getName());
            armorStands.forEach(ArmorStand::remove);
        }

        armorStands = spawnArmorStands(player, messageLines);
        playerMessages.put(playerId, armorStands);

        scheduleArmorStandUpdate(player, armorStands, messageLines.size());

        // Hide own hologram messages from the player who sent them
        if (!plugin.getConfigManager().isSeeOwnMessageHologram()) {
            hideHologramFromPlayer(player, armorStands);
            plugin.getLogger().info("Hiding ArmorStands from player " + player.getName());
        }
    }

    // Existing methods...

    private boolean hasRequiredPermission(Player player) {
        boolean hasPermission = plugin.getConfigManager().getGroups().values().stream()
                .anyMatch(group -> player.hasPermission(group.getPermission()));
        plugin.getLogger().info("Checking permissions for player " + player.getName() + ": " + hasPermission);
        return hasPermission;
    }

    private MessageGroup getHighestWeightGroup(Player player) {
        MessageGroup highestWeightGroup = plugin.getConfigManager().getGroups().values().stream()
                .filter(group -> player.hasPermission(group.getPermission()))
                .max((g1, g2) -> Integer.compare(g1.getWeight(), g2.getWeight()))
                .orElse(null);
        plugin.getLogger().info("Highest weight group for player " + player.getName() + ": " + (highestWeightGroup != null ? highestWeightGroup.getMessageFormat() : "None"));
        return highestWeightGroup;
    }

    private String formatMessage(Player player, String message) {
        MessageGroup group = getHighestWeightGroup(player);
        String format = group != null ? group.getMessageFormat() : "%player%: <message>";
        String formattedMessage = format.replace("%player%", player.getName()).replace("<message>", message);
        formattedMessage = plugin.getConfigManager().applyPlaceholders(player, formattedMessage);
        formattedMessage = plugin.getConfigManager().color(formattedMessage);

        plugin.getLogger().info("Formatted message for player " + player.getName() + ": " + formattedMessage);
        return formattedMessage;
    }

    private String getInitialColorCode(String formattedMessage) {
        int hexColorIndex = formattedMessage.indexOf("§x");
        if (hexColorIndex != -1 && hexColorIndex + 13 <= formattedMessage.length()) {
            return formattedMessage.substring(hexColorIndex, hexColorIndex + 14);
        }
        int lastColorCodeIndex = formattedMessage.lastIndexOf('§');
        if (lastColorCodeIndex != -1 && lastColorCodeIndex + 1 < formattedMessage.length()) {
            return formattedMessage.substring(lastColorCodeIndex, lastColorCodeIndex + 2);
        }
        return "";
    }

    private List<String> splitMessageIntoLines(String message, String initialColorCode) {
        int lineLength = plugin.getConfigManager().getLineLength();
        StringBuilder formattedMessage = new StringBuilder();
        String currentColor = initialColorCode;

        while (message.length() > lineLength) {
            int splitIndex = message.lastIndexOf(' ', lineLength);
            if (splitIndex == -1) {
                splitIndex = lineLength;
            }

            String line = message.substring(0, splitIndex);
            line = applyCurrentColor(line, currentColor);

            formattedMessage.append(line).append('\n');

            message = message.substring(splitIndex).trim();
        }

        message = applyCurrentColor(message, currentColor);
        formattedMessage.append(message);

        List<String> lines = List.of(formattedMessage.toString().split("\n"));
        plugin.getLogger().info("Split message into lines: " + lines);
        return lines;
    }

    private String applyCurrentColor(String line, String currentColor) {
        if (!currentColor.isEmpty() && !line.startsWith(currentColor)) {
            return currentColor + line;
        }
        return line;
    }

    private List<ArmorStand> spawnArmorStands(Player player, List<String> messages) {
        List<ArmorStand> armorStands = new ArrayList<>();
        double yOffset = plugin.getConfigManager().getYOffset() + (0.25 * messages.size());

        for (int i = 0; i < messages.size(); i++) {
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(
                    player.getLocation().add(0, yOffset - (0.25 * i), 0),
                    EntityType.ARMOR_STAND
            );
            armorStand.setVisible(false); // Set invisible from the start
            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(messages.get(i));
            armorStand.setMetadata(armorStandMetadataKey, new FixedMetadataValue(plugin, true));

            plugin.getLogger().info("Spawned ArmorStand for player " + player.getName() + " with message: " + messages.get(i));

            armorStands.add(armorStand);
        }

        return armorStands;
    }

    private void scheduleArmorStandUpdate(Player player, List<ArmorStand> armorStands, int numberOfLines) {
        int baseDuration = plugin.getConfigManager().getDisplayDuration();
        int increasePerLine = plugin.getConfigManager().getIncreaseDisplayDurationPerLine();
        int totalDuration = baseDuration + (increasePerLine * numberOfLines);
        int updateInterval = plugin.getConfigManager().getTextFollowUpdateInterval();

        new BukkitRunnable() {
            int remainingTicks = totalDuration * 20;

            public void run() {
                if (!armorStands.isEmpty() && player.isOnline()) {
                    for (int i = 0; i < armorStands.size(); i++) {
                        ArmorStand armorStand = armorStands.get(i);
                        if (!armorStand.isDead()) {
                            armorStand.teleport(player.getLocation().add(0, plugin.getConfigManager().getYOffset() + (0.25 * (armorStands.size() - i)), 0));
                        }
                    }
                    remainingTicks -= updateInterval;
                    if (remainingTicks <= 0) {
                        armorStands.forEach(ArmorStand::remove);
                        plugin.getLogger().info("Removing ArmorStands for player " + player.getName() + " after duration.");
                        this.cancel();
                    }
                } else {
                    armorStands.forEach(ArmorStand::remove);
                    plugin.getLogger().info("Removing ArmorStands for player " + player.getName() + " as player is offline or armor stands are empty.");
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, updateInterval);
    }

    private void hideHologramFromPlayer(Player player, List<ArmorStand> armorStands) {
        if (armorStands.isEmpty()) {
            plugin.getLogger().warning("No ArmorStands to hide for player " + player.getName());
            return;
        }

        int[] entityIds = armorStands.stream().mapToInt(ArmorStand::getEntityId).toArray();
        if (entityIds.length == 0) {
            plugin.getLogger().warning("No entity IDs found to hide for player " + player.getName());
            return;
        }

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        try {
            packet.getIntLists().write(0, Arrays.stream(entityIds).boxed().collect(Collectors.toList()));
            protocolManager.sendServerPacket(player, packet);
            plugin.getLogger().info("Hiding ArmorStands with IDs " + Arrays.toString(entityIds) + " from player " + player.getName());
        } catch (FieldAccessException e) {
            plugin.getLogger().severe("Exception in hideHologramFromPlayer: " + e.getMessage());
        }
    }

    public void clearAllArmorStands() {
        playerMessages.values().forEach(armorStands -> armorStands.forEach(ArmorStand::remove));
        playerMessages.clear();
        plugin.getLogger().info("Cleared all ArmorStands.");
    }
}