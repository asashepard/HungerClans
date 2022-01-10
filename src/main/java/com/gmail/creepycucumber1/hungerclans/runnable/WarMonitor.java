package com.gmail.creepycucumber1.hungerclans.runnable;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WarMonitor {

    private HungerClans plugin;

    public WarMonitor(HungerClans plugin) {
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
                        plugin.getWarManager().endWar(war, plugin.getWarManager().getSide2(war));
                    }
                    else if(plugin.getDataManager().getConfig().getConfigurationSection("clans." +
                            plugin.getWarManager().getSide2(war)) == null) { //side 2 is disbanded
                        plugin.getWarManager().endWar(war, plugin.getWarManager().getSide1(war));
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

}
