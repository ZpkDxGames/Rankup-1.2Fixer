package com.comonier;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RankMenu {

    private final Rankup plugin;

    public RankMenu(Rankup plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, int page) {
        MessageManager mm = plugin.getMessageManager();
        String title = mm.format(mm.getMessage("menu.title").replace("%page%", String.valueOf(page)));
        Inventory inv = Bukkit.createInventory(null, 54, title);

        int start = (page - 1) * 45;
        int playerRankId = plugin.getDatabase().getPlayerRank(player.getUniqueId());

        for (int i = 0; i < 45; i++) {
            int rankId = start + i;
            if (rankId > 100) break;
            RankManager.Rank rank = plugin.getRankManager().getRank(rankId);
            if (rank != null) inv.setItem(i, createRankItem(rank, playerRankId));
        }

        if (page > 1) inv.setItem(45, createItem(Material.ARROW, mm.getMessage("menu.previous-page")));
        if (plugin.getRankManager().getRanks().size() > (start + 45) || (start + 45) < 100) {
            inv.setItem(53, createItem(Material.ARROW, mm.getMessage("menu.next-page")));
        }

        player.openInventory(inv);
    }

    private ItemStack createRankItem(RankManager.Rank rank, int playerRankId) {
        MessageManager mm = plugin.getMessageManager();
        Material material;
        String status;

        if (playerRankId >= rank.getId()) {
            material = Material.LIME_STAINED_GLASS_PANE;
            status = mm.getMessage("menu.status-completed");
        } else if (playerRankId + 1 == rank.getId()) {
            material = Material.YELLOW_STAINED_GLASS_PANE;
            status = mm.getMessage("menu.status-next");
        } else {
            material = Material.RED_STAINED_GLASS_PANE;
            status = mm.getMessage("menu.status-locked");
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(mm.format(rank.getDisplayName()));
            meta.setLore(buildLore(rank, status));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }
        return item;
    }

    private List<String> buildLore(RankManager.Rank rank, String status) {
        MessageManager mm = plugin.getMessageManager();
        FileConfiguration ranksConfig = plugin.getRankManager().getRanksConfig();
        String path = "ranks." + rank.getId() + ".menu.lore";
        List<String> configuredLore = ranksConfig.getStringList(path);

        if (configuredLore == null || configuredLore.isEmpty()) {
            return buildLegacyLore(rank, status);
        }

        List<String> lore = new ArrayList<>();
        List<String> rewardLines = ranksConfig.getStringList("ranks." + rank.getId() + ".rewards.display");

        for (String line : configuredLore) {
            if (line == null) continue;

            if (line.trim().equalsIgnoreCase("%rewards%")) {
                if (rewardLines != null && !rewardLines.isEmpty()) {
                    for (String rewardLine : rewardLines) {
                        lore.add(mm.format(applyPlaceholders(rewardLine, rank, status)));
                    }
                }
                continue;
            }

            lore.add(mm.format(applyPlaceholders(line, rank, status)));
        }

        return lore;
    }

    private List<String> buildLegacyLore(RankManager.Rank rank, String status) {
        MessageManager mm = plugin.getMessageManager();
        List<String> lore = new ArrayList<>();
        lore.add(mm.format(mm.getMessage("menu.item-lore-status").replace("%status%", status)));

        if (rank.getId() > 0) {
            lore.add("");
            lore.add(mm.format(mm.getMessage("menu.item-lore-req-title")));
            String moneyFormatted = plugin.formatMoney(rank.getReqMoney());
            lore.add(mm.format(mm.getMessage("menu.item-lore-money").replace("%amount%", moneyFormatted)));
            lore.add(mm.format(mm.getMessage("menu.item-lore-xp").replace("%amount%", String.valueOf(rank.getReqXp()))));
            lore.add(mm.format(mm.getMessage("menu.item-lore-time").replace("%amount%", String.valueOf(rank.getReqTime()))));

            if (rank.getCommands() != null && !rank.getCommands().isEmpty()) {
                lore.add(mm.format("§7- Blocos de Proteção: §e+" + (rank.getId() * 1000)));
            }
        }

        return lore;
    }

    private String applyPlaceholders(String line, RankManager.Rank rank, String status) {
        return line
                .replace("%status%", status)
                .replace("%rank_id%", String.valueOf(rank.getId()))
                .replace("%rank_display%", rank.getDisplayName())
                .replace("%rank_tag%", rank.getTag() == null ? "" : rank.getTag())
                .replace("%money%", plugin.formatMoney(rank.getReqMoney()))
                .replace("%xp%", String.valueOf(rank.getReqXp()))
                .replace("%playtime%", String.valueOf(rank.getReqTime()))
                .replace("%commands_count%", String.valueOf(rank.getCommands() == null ? 0 : rank.getCommands().size()))
                .replace("%permissions_count%", String.valueOf(rank.getPermissions() == null ? 0 : rank.getPermissions().size()));
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getMessageManager().format(name));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }
        return item;
    }
}
