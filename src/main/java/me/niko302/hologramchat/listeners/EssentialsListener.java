package me.niko302.hologramchat.listeners;

import com.earth2me.essentials.Essentials;
import me.niko302.hologramchat.HologramChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class EssentialsListener implements Listener {

    private final HologramChat plugin;
    private final Essentials essentials;

    public EssentialsListener(HologramChat plugin) {
        this.plugin = plugin;
        Plugin ess = plugin.getServer().getPluginManager().getPlugin("Essentials");
        if (ess instanceof Essentials) {
            this.essentials = (Essentials) ess;
        } else {
            throw new IllegalStateException("Essentials not found!");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (isPrivateMessageCommand(command)) {
            Player sender = event.getPlayer();
            String[] args = command.split(" ");
            if (args.length > 2) {
                Player recipient = Bukkit.getPlayer(args[1]);
                if (recipient != null && recipient.isOnline()) {
                    String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                    // Display hologram to sender and recipient
                    plugin.getChatListener().displayPrivateMessageHologram(sender, recipient, message);
                }
            }
        }
    }

    private boolean isPrivateMessageCommand(String command) {
        String[] privateMessageCommands = {"/msg", "/tell", "/whisper", "/w", "/t", "/pm"};
        for (String cmd : privateMessageCommands) {
            if (command.toLowerCase().startsWith(cmd + " ")) {
                return true;
            }
        }
        return false;
    }
}