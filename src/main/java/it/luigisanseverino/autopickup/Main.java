package it.luigisanseverino.autopickup;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin {

    // Set dei giocatori con autopickup attivo
    private final Set<UUID> activePickup = new HashSet<>();

    @Override
    public void onEnable() {
        // Registra il listener degli eventi
        getServer().getPluginManager().registerEvents(
                new PickupListener(this), this
        );

        // Registra il comando /autopickup
        getCommand("autopickup").setExecutor(new PickupCommand(this));

        getLogger().info("AutoPickup avviato!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AutoPickup disattivato.");
    }

    // Controlla se il giocatore ha l'autopickup attivo
    public boolean isActive(UUID uuid) {
        return activePickup.contains(uuid);
    }

    // Attiva o disattiva l'autopickup per un giocatore
    public void toggle(UUID uuid) {
        if (activePickup.contains(uuid)) {
            activePickup.remove(uuid);
        } else {
            activePickup.add(uuid);
        }
    }
}