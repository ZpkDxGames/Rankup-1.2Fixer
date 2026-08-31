package com.comonier;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RankupCommand implements CommandExecutor {

    private final Rankup plugin;

    public RankupCommand(Rankup plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;
        MessageManager mm = plugin.getMessageManager();
        int currentRankId = plugin.getDatabase().getPlayerRank(player.getUniqueId());
        RankManager.Rank nextRank = plugin.getRankManager().getNextRank(currentRankId);

        if (nextRank == null) {
            mm.sendMessage(player, "rankup.max-rank");
            return true;
        }

        double playerMoney = Rankup.getEconomy().getBalance(player);
        int playerXp = player.getLevel();
        int playerTime = plugin.getRankManager().getPlayerPlayTimeMinutes(player);

        if (playerMoney >= nextRank.getReqMoney() && playerXp >= nextRank.getReqXp() && playerTime >= nextRank.getReqTime()) {
            Rankup.getEconomy().withdrawPlayer(player, nextRank.getReqMoney());
            player.setLevel(playerXp - nextRank.getReqXp());
            plugin.getDatabase().setPlayerRank(player.getUniqueId(), nextRank.getId());

            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                for (String perm : nextRank.getPermissions()) {
                    user.data().add(Node.builder(perm).build());
                }
                lp.getUserManager().saveUser(user);
            }

            for (String cmd : nextRank.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
            }

            String formattedRankName = mm.format(nextRank.getDisplayName());
            String formattedCost = plugin.formatMoney(nextRank.getReqMoney());
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%rank_display%", formattedRankName);
            placeholders.put("%money_cost%", formattedCost);
            mm.sendMessage(player, "rankup.success", placeholders);

            if (plugin.getConfig().getBoolean("settings.broadcast-rankup")) {
                String globalMsg = mm.getMessage("broadcast.rankup")
                        .replace("%player%", player.getName())
                        .replace("%rank_display%", formattedRankName);
                Bukkit.broadcastMessage(globalMsg);
            }

            plugin.getDiscordWebhook().sendRankupMessage(player.getName(),
                    nextRank.getDisplayName().replaceAll("(?i)&[0-9A-FK-ORX]", ""));
        } else {
            mm.sendMessage(player, "rankup.requirements-not-met");
        }

        return true;
    }
}
