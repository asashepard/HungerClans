package com.gmail.creepycucumber1.hungerclans.event;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        plugin.getPlayerManager().setUpdateTimeLastToNow(player);

        if(plugin.getPlayerManager().getCombatLogged(player)) {
            player.setHealth(0.0D);
            plugin.getPlayerManager().setCombatLogged(player,false);
        }

        plugin.getDiscordManager().updateUserRoles(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.getGUIManager().onLeave(e.getPlayer());
    }

    @EventHandler
    public void onDamageByPlayer(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        if(player.equals(damager)) return;
        plugin.getPlayerManager().setLastDamagedByPlayerToNow(player);

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
            plugin.getWarManager().addScore(war, opposition, plugin.getConfigManager().getConfig().getInt("integer.deathScore"));
        }
        if(player.getKiller() == null) return;
        Player killer = player.getKiller();
        if(!plugin.getClanManager().isInClan(killer)) return;
        for(String war : wars) {
            String opposition = plugin.getWarManager().getOpposition(war, clanName);
            if(plugin.getClanManager().getClan(killer).equalsIgnoreCase(opposition)) {
                plugin.getWarManager().addScore(war, opposition,
                        plugin.getConfigManager().getConfig().getInt("integer.killScore") - plugin.getConfigManager().getConfig().getInt("integer.deathScore"));
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
            plugin.getWarManager().addScore(war, opposition, plugin.getConfigManager().getConfig().getInt("integer.totemScore"));
        }
    }

    @EventHandler
    public void onProximityLeave(PlayerQuitEvent e) { //"combat logging" - proximity and time
        Player player = e.getPlayer();
        long now = Instant.now().toEpochMilli();
        boolean combatLogged = false;

        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.checkCombatLog")) return;
        if(player.getStatistic(Statistic.TIME_SINCE_DEATH) < 15 * 20) return;

        if(plugin.getConfigManager().getConfig().getBoolean("boolean.antiCombatLogPlus")) {
            if(player.getLocation().getNearbyPlayers(15).size() > 0 ||
                    (player.isGliding() && player.getLocation().getNearbyPlayers(40, 100).size() > 0))
                if(now - plugin.getPlayerManager().getLastDamagedByPlayer(player) <= (long) plugin.getConfigManager().getConfig().getInt("integer.combatLogSeconds") * 1000) {
                    plugin.getPlayerManager().setCombatLogged(player, true);
                    combatLogged = true;
                }
        }

        if(!plugin.getClanManager().isInClan(player)) return;
        String clanName = plugin.getClanManager().getClan(player);

        if(!plugin.getWarManager().isInWar(clanName)) return;
        for(String war : plugin.getWarManager().getWars(clanName)) {
            String otherClanName = plugin.getWarManager().getOpposition(war, clanName);
            for(String str : plugin.getClanManager().getMembers(otherClanName)) {
                if(Bukkit.getOfflinePlayer(UUID.fromString(str)).isOnline()) {

                    Player enemy = Bukkit.getOfflinePlayer(UUID.fromString(str)).getPlayer();
                    if(player.getLocation().getNearbyPlayers(15).contains(enemy) ||
                            (player.isGliding() && player.getLocation().getNearbyPlayers(40, 100).contains(enemy))) {
                        long lastDamagedByEnemy = plugin.getPlayerManager().getLastDamagedByEnemy(player);
                        if(now - lastDamagedByEnemy <= (long) plugin.getConfigManager().getConfig().getInt("integer.combatLogSeconds") * 1000)
                            combatLogged = true;
                    }
                }
            }
            if(combatLogged)
                plugin.getWarManager().addScore(war, otherClanName,
                        plugin.getConfigManager().getConfig().getInt("integer.combatLogScore") + (int) (Math.random() * plugin.getConfigManager().getConfig().getInt("integer.combatLogRandom") + 1));
        }
        if(combatLogged) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(TextUtil.convertColor("&9&lCOMBAT LOG &8» &f" + player.getName() +
                        plugin.getClanManager().getColor(clanName) + " [" +
                        plugin.getClanManager().getCode(clanName) + "] &7left during battle!"));
            }
            Bukkit.getLogger().info("COMBAT LOG » " + player.getName() + " [" +
                    plugin.getClanManager().getCode(clanName) + "] left during battle!");
        }

    }

    @EventHandler
    public void onWarCircumvent(PlayerCommandPreprocessEvent e) { //teleporting (proximity), /suicide
        Player player = e.getPlayer();

        if(!plugin.getClanManager().isInClan(player)) return;
        String clanName = plugin.getClanManager().getClan(player);

        if(plugin.getWarManager().isInWar(clanName)) {
            if(e.getMessage().equalsIgnoreCase("/suicide")) {
                player.sendMessage(TextUtil.convertColor("&cYou're in war, so fight!"));
                e.setCancelled(true);
            }
            else if(e.getMessage().toLowerCase().contains("home") && !e.getMessage().toLowerCase().contains("sethome") ||
                    e.getMessage().toLowerCase().contains("tpaccept") ||
                    e.getMessage().toLowerCase().contains("tpask") ||
                    e.getMessage().toLowerCase().contains("leave") ||
                    e.getMessage().toLowerCase().contains("warp")) {
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMine(BlockBreakEvent e) {
        plugin.getPlayerManager().addMinedBlocks(e.getPlayer(), 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {
        plugin.getPlayerManager().addPlacedBlocks(e.getPlayer(), 1);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        plugin.getGUIManager().onClose(Bukkit.getPlayer(e.getPlayer().getUniqueId()), e.getView());
    }

}
