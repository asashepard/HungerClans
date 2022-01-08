package com.gmail.creepycucumber1.hungerclans;

import com.gmail.creepycucumber1.hungerclans.clan.ClanManager;
import com.gmail.creepycucumber1.hungerclans.command.ClanCommand;
import com.gmail.creepycucumber1.hungerclans.command.CommandBase;
import com.gmail.creepycucumber1.hungerclans.data.DataManager;
import com.gmail.creepycucumber1.hungerclans.event.EventManager;
import com.gmail.creepycucumber1.hungerclans.gui.GUIManager;
import com.gmail.creepycucumber1.hungerclans.nametag.NametagManager;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public final class HungerClans extends JavaPlugin {

    private ArrayList<CommandBase> commands;
    private ClanManager clanManager;
    private DataManager dataManager;
    private GUIManager guiManager;
    private NametagManager nametagManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        commands = registerCommands();

        clanManager = new ClanManager(this);
        dataManager = new DataManager(this);
        guiManager = new GUIManager(this);

        nametagManager = new NametagManager(this);
        nametagManager.nametag();

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new EventManager(this), this);

        getLogger().info("HungerClans has started.");
    }

    @Override
    public void onDisable() {
        //.
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        for(CommandBase c : commands){
            if(c.getCommand().equalsIgnoreCase(label)|| (c.getAliases()!=null&&c.getAliases().contains(label))){
                boolean result = c.execute(sender, args);
                if(!result){
                    sender.sendMessage(TextUtil.convertColor("&cInvalid command usage!\n&7/" + label + " " + c.getUsage()));
                }
            }
        }

        return true;
    }

    private ArrayList<CommandBase> registerCommands() {
        ArrayList<CommandBase> commands = new ArrayList<>();
        commands.add(new ClanCommand(this));
        return commands;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }
    public DataManager getDataManager() {
        return dataManager;
    }
    public GUIManager getGUIManager() {
        return guiManager;
    }

}
