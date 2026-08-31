package com.comonier;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class RankListener implements Listener {
    private final Rankup plugin;
    public RankListener(Rankup plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CompletableFuture.runAsync(() -> {
            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().getUser(player.getUniqueId());
            if (user == null) return;

            int currentRankId = plugin.getDatabase().getPlayerRank(player.getUniqueId());
            boolean modified = false;
            for (int i = 0; i <= currentRankId; i++) {
                RankManager.Rank rank = plugin.getRankManager().getRank(i);
                if (rank == null || rank.getPermissions() == null) continue;
                for (String perm : rank.getPermissions()) {
                    if (!user.getCachedData().getPermissionData().checkPermission(perm).asBoolean()) {
                        user.data().add(Node.builder(perm).build());
                        modified = true;
                    }
                }
            }
            if (modified) {
                lp.getUserManager().saveUser(user);
                plugin.getLogger().info("Permissoes retroativas aplicadas para: " + player.getName());
            }
        });
    }
}
