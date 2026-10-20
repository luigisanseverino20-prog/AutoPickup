package it.luigisanseverino.autopickup;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PickupCommand implements CommandExecutor {

    private final Main plugin;

    public PickupCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {

        // Solo i giocatori possono usare questo comando
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo i giocatori possono usare questo comando.");
            return true;
        }

        // Attiva o disattiva l'autopickup
        plugin.toggle(player.getUniqueId());

        if (plugin.isActive(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN +
                    "[AutoPickup] ✦ Attivato — tieni Shift mentre rompi i blocchi!");
        } else {
            player.sendMessage(ChatColor.RED +
                    "[AutoPickup] ✦ Disattivato.");
        }

        return true;
    }
}
