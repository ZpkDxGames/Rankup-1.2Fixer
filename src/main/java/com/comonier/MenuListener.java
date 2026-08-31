package com.comonier;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {
    private final Rankup plugin;
    public MenuListener(Rankup plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        String menuTitleBase = plugin.getMessageManager().getMessage("menu.title").replace("%page%", "");
        if (!event.getView().getTitle().contains(menuTitleBase)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        int currentPage = 1;
        try {
            String[] parts = event.getView().getTitle().split(" ");
            currentPage = Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception ignored) {}

        if (item.getType() == Material.ARROW) {
            if (event.getSlot() == 45 && currentPage > 1) new RankMenu(plugin).open(player, currentPage - 1);
            else if (event.getSlot() == 53) new RankMenu(plugin).open(player, currentPage + 1);
            return;
        }

        if (event.getClick() == ClickType.RIGHT && item.getType() == Material.YELLOW_STAINED_GLASS_PANE) {
            player.closeInventory();
            player.performCommand("rankup");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        String menuTitleBase = plugin.getMessageManager().getMessage("menu.title").replace("%page%", "");
        if (event.getView().getTitle().contains(menuTitleBase)) event.setCancelled(true);
    }
}
