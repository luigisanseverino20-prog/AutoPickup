package it.luigisanseverino.autopickup;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PickupListener implements Listener {

    private final Main plugin;

    // Lista dei minerali che supportano il vein miner
    private static final Set<Material> VEIN_MATERIALS = new HashSet<>();

    static {
        VEIN_MATERIALS.add(Material.COAL_ORE);
        VEIN_MATERIALS.add(Material.DEEPSLATE_COAL_ORE);
        VEIN_MATERIALS.add(Material.IRON_ORE);
        VEIN_MATERIALS.add(Material.DEEPSLATE_IRON_ORE);
        VEIN_MATERIALS.add(Material.GOLD_ORE);
        VEIN_MATERIALS.add(Material.DEEPSLATE_GOLD_ORE);
        VEIN_MATERIALS.add(Material.DIAMOND_ORE);
        VEIN_MATERIALS.add(Material.DEEPSLATE_DIAMOND_ORE);
        VEIN_MATERIALS.add(Material.EMERALD_ORE);
        VEIN_MATERIALS.add(Material.DEEPSLATE_EMERALD_ORE);
        VEIN_MATERIALS.add(Material.LAPIS_ORE);
        VEIN_MATERIALS.add(Material.DEEPSLATE_LAPIS_ORE);
        VEIN_MATERIALS.add(Material.REDSTONE_ORE);
        VEIN_MATERIALS.add(Material.DEEPSLATE_REDSTONE_ORE);
        VEIN_MATERIALS.add(Material.COPPER_ORE);
        VEIN_MATERIALS.add(Material.DEEPSLATE_COPPER_ORE);
        VEIN_MATERIALS.add(Material.NETHER_QUARTZ_ORE);
        VEIN_MATERIALS.add(Material.NETHER_GOLD_ORE);
        VEIN_MATERIALS.add(Material.ANCIENT_DEBRIS);
    }

    public PickupListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Controlla se il giocatore ha l'autopickup attivo
        if (!plugin.isActive(player.getUniqueId())) return;

        // Controlla se il giocatore tiene Shift
        if (!player.isSneaking()) return;

        // Controlla se il blocco è un minerale supportato
        if (!VEIN_MATERIALS.contains(event.getBlock().getType())) {
            // Se non è un minerale — pickup normale
            normalPickup(event, player);
            return;
        }

        // Vein Miner — trova tutta la vena
        event.setDropItems(false);
        List<Block> vein = findVein(event.getBlock(), event.getBlock().getType(), new HashSet<>(), 64);

        for (Block block : vein) {
            // Ottieni i drop del blocco
            var drops = block.getDrops(player.getInventory().getItemInMainHand());
            // Rompi il blocco
            block.breakNaturally();
            block.setType(Material.AIR);
            // Aggiungi i drop all'inventario
            for (ItemStack item : drops) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    player.sendMessage(ChatColor.RED + "[AutoPickup] Inventario pieno!");
                } else {
                    player.getInventory().addItem(item);
                }
            }
        }

        player.sendMessage(ChatColor.GOLD + "[AutoPickup] ⛏ Vena raccolta: " + vein.size() + " blocchi!");
    }

    // Raccolta normale per blocchi non minerali
    private void normalPickup(BlockBreakEvent event, Player player) {
        var drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand());
        event.setDropItems(false);
        for (ItemStack item : drops) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                player.sendMessage(ChatColor.RED + "[AutoPickup] Inventario pieno!");
            } else {
                player.getInventory().addItem(item);
            }
        }
    }

    // Algoritmo ricorsivo per trovare tutta la vena connessa
    private List<Block> findVein(Block block, Material type, Set<Block> visited, int maxBlocks) {
        List<Block> vein = new ArrayList<>();

        if (visited.contains(block)) return vein;
        if (block.getType() != type) return vein;
        if (visited.size() >= maxBlocks) return vein;

        visited.add(block);
        vein.add(block);

        // Controlla i 6 blocchi adiacenti
        int[][] directions = {
                {1,0,0}, {-1,0,0},
                {0,1,0}, {0,-1,0},
                {0,0,1}, {0,0,-1}
        };

        for (int[] dir : directions) {
            Block neighbor = block.getRelative(dir[0], dir[1], dir[2]);
            vein.addAll(findVein(neighbor, type, visited, maxBlocks));
        }

        return vein;
    }
}