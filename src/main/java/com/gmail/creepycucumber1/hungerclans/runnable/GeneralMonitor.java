package com.gmail.creepycucumber1.hungerclans.runnable;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class GeneralMonitor {

    private HungerClans plugin;

    public GeneralMonitor(HungerClans plugin) {
        this.plugin = plugin;
    }

    public void monitorWars() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {

                ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars");
                Date currentDate = Calendar.getInstance().getTime();
                DateFormat dayFormat = new SimpleDateFormat("dd");
                int currentDay = Integer.parseInt(dayFormat.format(currentDate));
                String today = String.valueOf(currentDay);

                for(String war : cfg.getKeys(false)) {
                    if(cfg.get(war + ".endsByTime").equals(today)) { //time reaches its end
                        plugin.getWarManager().endWar(war);
                    }
                    else if(plugin.getDataManager().getConfig().getConfigurationSection("clans." +
                            plugin.getWarManager().getSide1(war)) == null) { //side 1 is disbanded
                        plugin.getWarManager().endWar(war, plugin.getWarManager().getSide2(war), false);
                    }
                    else if(plugin.getDataManager().getConfig().getConfigurationSection("clans." +
                            plugin.getWarManager().getSide2(war)) == null) { //side 2 is disbanded
                        plugin.getWarManager().endWar(war, plugin.getWarManager().getSide1(war), false);
                    }
                }

                ConfigurationSection cfg2 = plugin.getDataManager().getConfig().getConfigurationSection("clans");
                for(String clan : cfg2.getKeys(false)) {
                    for(String otherClan : cfg2.getKeys(false))
                        if(plugin.getClanManager().declarationsBlocked(clan, otherClan)) //clan can't declare against otherClan
                            if(plugin.getClanManager().getDeclareAgainDay(clan, otherClan).equalsIgnoreCase(today))
                                plugin.getClanManager().removeDeclarationBlock(clan, otherClan);
                }

            }
        }, 0, 2000);

    }

    public void monitorPlayers() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {

                if(plugin.getPlayerManager().checkUpdate()) {
                    for(String uuid : plugin.getDataManager().getConfig().getConfigurationSection("players").getKeys(false)) {
                        plugin.getPlayerManager().resetTimePlayedToday(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
                        plugin.getPlayerManager().setReceivedReward(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), false);
                    }
                    rewardClanMembers();
                    Bukkit.getLogger().info("Daily playtime reset.");
                }

                boolean dailyReward = plugin.getConfigManager().getConfig().getBoolean("boolean.dailyReward");

                for(Player player : Bukkit.getOnlinePlayers()) {

                    plugin.getPlayerManager().updateTimePlayed(player);
                    plugin.getPlayerManager().setUpdateTimeLastToNow(player);

                    if(!dailyReward) return;

                    //daily reward
                    if(!plugin.getPlayerManager().receivedReward(player) &&
                            plugin.getPlayerManager().getTimePlayedToday(player) >= (long) plugin.getConfigManager().getConfig().getInt("integer.dailyRewardMinutes") * 60000) {

                        int reward = plugin.getConfigManager().getConfig().getInt("integer.dailyReward");
                        if(reward != 0) player.sendMessage(TextUtil.convertColor("&aWelcome to a new day on &lHunger&2&lMC&r&a. Take this &2$" + reward + "&a for your journey."));
                        plugin.getVault().depositPlayer(player, reward);
                        plugin.getPlayerManager().setReceivedReward(player, true);

                    }

                }

            }
        }, 0, 500);

    }

    private void rewardClanMembers() {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        int base = plugin.getConfigManager().getConfig().getInt("integer.clanPointBase");
        int boost = plugin.getConfigManager().getConfig().getInt("integer.clanMemberBoost");
        for(String clan : cfg.getKeys(false)) {
            int members = cfg.getStringList(clan + ".members").size();
            plugin.getClanManager().addPoints(clan, (int) (Math.random() * (boost / 10.0 * members) + base));
        }
    }

}
