package com.gmail.creepycucumber1.hungerclans.event;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class EventManager implements Listener {

    private HungerClans plugin;

    public EventManager(HungerClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString()) == null)
            plugin.getPlayerManager().createNewPlayer(player);
    }

    @EventHandler
    public void onDamageByEnemy(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        if(!plugin.getClanManager().isInClan(player)) return;
        String clanName = plugin.getClanManager().getClan(player);
        if(!plugin.getClanManager().isInClan(damager)) return;
        String otherClanName = plugin.getClanManager().getClan(damager);

        if(!plugin.getWarManager().areAtWar(clanName, otherClanName)) return;

        plugin.getPlayerManager().setLastDamagedByEnemyToNow(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        boolean res = plugin.getGUIManager().onClick(Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), e.getCurrentItem(), e.getView());
        if(res) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerWarDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();

        if(!plugin.getClanManager().isInClan(player)) return;
        String clanName = plugin.getClanManager().getClan(player);

        if(!plugin.getWarManager().isInWar(clanName)) return;
        ArrayList<String> wars = plugin.getWarManager().getWars(clanName);
        for(String war : wars) {
            String opposition = plugin.getWarManager().getOpposition(war, clanName);
            plugin.getWarManager().addPoints(war, opposition, 10);
        }
        if(player.getKiller() == null) return;
        Player killer = player.getKiller();
        if(!plugin.getClanManager().isInClan(killer)) return;
        for(String war : wars) {
            String opposition = plugin.getWarManager().getOpposition(war, clanName);
            if(plugin.getClanManager().getClan(killer).equalsIgnoreCase(opposition)) {
                plugin.getWarManager().addPoints(war, opposition, 10);
                return;
            }
        }
    }

    @EventHandler
    public void onWarTotemPop(EntityResurrectEvent e) {
        if(e.isCancelled()) return;
        if(!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();

        if(!plugin.getClanManager().isInClan(player)) return;
        String clanName = plugin.getClanManager().getClan(player);

        if(!plugin.getWarManager().isInWar(clanName)) return;
        ArrayList<String> wars = plugin.getWarManager().getWars(clanName);
        for(String war : wars) {
            String opposition = plugin.getWarManager().getOpposition(war, clanName);
            plugin.getWarManager().addPoints(war, opposition, 9);
        }
    }

    @EventHandler
    public void onWarProximityLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if(!plugin.getClanManager().isInClan(player)) return;
        String clanName = plugin.getClanManager().getClan(player);

        if(!plugin.getWarManager().isInWar(clanName)) return;
        boolean combatLogged = false;
        for(String war : plugin.getWarManager().getWars(clanName)) {
            String otherClanName = plugin.getWarManager().getOpposition(war, clanName);
            for(String str : plugin.getClanManager().getMembers(otherClanName)) {
                if(Bukkit.getOfflinePlayer(UUID.fromString(str)).isOnline()) {

                    Player enemy = Bukkit.getOfflinePlayer(UUID.fromString(str)).getPlayer();
                    if(player.getLocation().getNearbyPlayers(15).contains(enemy) ||
                            (player.isGliding() && player.getLocation().getNearbyPlayers(40, 100).contains(enemy))) {
                        long now = Instant.now().toEpochMilli();
                        long lastDamagedByEnemy = plugin.getPlayerManager().getLastDamagedByEnemy(player);
                        if(now - lastDamagedByEnemy < (15 * 1000 + 1))
                            combatLogged = true;
                    }
                }
            }
            if(combatLogged)
                plugin.getWarManager().addPoints(war, otherClanName, 3 + (int) (Math.random() * 9 + 1));
        }
        if(combatLogged) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(TextUtil.convertColor("&9COMBAT LOG &8» &f" + player.getName() +
                        ColorUtil.colorToStringCode(plugin.getClanManager().getColor(clanName)) + " [" +
                        plugin.getClanManager().getCode(clanName) + "] &7left their opponents hanging!"));
            }
            Bukkit.getLogger().info("COMBAT LOG » " + player.getName() + " [" +
                    plugin.getClanManager().getCode(clanName) + "] left their opponents hanging!");
        }

    }

    @EventHandler
    public void onWarCircumvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if(!plugin.getClanManager().isInClan(player)) return;
        String clanName = plugin.getClanManager().getClan(player);

        if(plugin.getWarManager().isInWar(clanName)) {
            if(e.getMessage().equalsIgnoreCase("/suicide")) {
                player.sendMessage(TextUtil.convertColor("&cYou're in war, so fight!"));
                e.setCancelled(true);
            }
            else if(e.getMessage().toLowerCase().contains("home") ||
                    e.getMessage().toLowerCase().contains("tpaccept") ||
                    e.getMessage().toLowerCase().contains("tpask") ||
                    e.getMessage().toLowerCase().contains("leave")) {
                //proximity check
                for(String war : plugin.getWarManager().getWars(clanName)) {
                    String otherClanName = plugin.getWarManager().getOpposition(war, clanName);
                    for(String str : plugin.getClanManager().getMembers(otherClanName)) {
                        if(Bukkit.getOfflinePlayer(UUID.fromString(str)).isOnline()) {

                            Player enemy = Bukkit.getOfflinePlayer(UUID.fromString(str)).getPlayer();
                            if(player.getLocation().getNearbyPlayers(50, 40).contains(enemy)) {
                                player.sendMessage(TextUtil.convertColor("&cYou can't do that - the enemy is near!"));
                                e.setCancelled(true);
                            }

                        }
                    }
                }
            }
        }

    }

}
