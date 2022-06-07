package com.gmail.creepycucumber1.hungerclans;

import com.gmail.creepycucumber1.hungerclans.clan.ClanManager;
import com.gmail.creepycucumber1.hungerclans.clan.PlayerManager;
import com.gmail.creepycucumber1.hungerclans.clan.WarManager;
import com.gmail.creepycucumber1.hungerclans.command.*;
import com.gmail.creepycucumber1.hungerclans.data.ConfigManager;
import com.gmail.creepycucumber1.hungerclans.data.DataManager;
import com.gmail.creepycucumber1.hungerclans.event.EventManager;
import com.gmail.creepycucumber1.hungerclans.gui.GUIManager;
import com.gmail.creepycucumber1.hungerclans.discord.DiscordManager;
import com.gmail.creepycucumber1.hungerclans.runnable.NametagManager;
import com.gmail.creepycucumber1.hungerclans.runnable.Teleport;
import com.gmail.creepycucumber1.hungerclans.runnable.GeneralMonitor;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import github.scarsz.discordsrv.DiscordSRV;
import net.ess3.api.IEssentials;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Calendar;

public final class HungerClans extends JavaPlugin {

    private IEssentials ess;
    private Economy vault;
    private ArrayList<CommandBase> commands;
    private ClanManager clanManager;
    private WarManager warManager;
    private PlayerManager playerManager;
    private DataManager dataManager;
    private ConfigManager configManager;
    private GUIManager guiManager;
    private NametagManager nametagManager;
    private GeneralMonitor generalMonitor;
    private Teleport teleport;
    private DiscordManager discordManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        dataManager = new DataManager(this);
        configManager = new ConfigManager(this);
        clanManager = new ClanManager(this);
        playerManager = new PlayerManager(this);
        warManager = new WarManager(this);
        guiManager = new GUIManager(this);
        nametagManager = new NametagManager(this);

        nametagManager = new NametagManager(this);
        generalMonitor = new GeneralMonitor(this);
        teleport = new Teleport(this);

        nametagManager.nametagSlow();
        nametagManager.nametagCheck();
        generalMonitor.monitorWars();
        generalMonitor.monitorPlayers();

        this.discordManager = new DiscordManager(this);
        DiscordSRV.api.subscribe(discordManager);

        commands = new ArrayList<>(registerCommands());

        try{
            ess = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");

            if(ess==null){
                getLogger().warning("HungerClans failed to hook in with Essentials!");
            }
        }
        catch(Exception ex){
            getLogger().warning("HungerClans failed to hook in with Essentials!");
        }

        if(getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if(rsp != null){
                vault = rsp.getProvider();
            }
        }

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new EventManager(this), this);

        getLogger().info("HungerClans has started.");
    }

    @Override
    public void onDisable() {
        DiscordSRV.api.unsubscribe(discordManager);
        getLogger().info("HungerClans has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        for(CommandBase c : commands)
            if(c.getCommand().equalsIgnoreCase(label) || (c.getAliases() != null && c.getAliases().contains(label))) {
                boolean result = c.execute(sender, args);
                if(!result)
                    sender.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cInvalid command usage!\n&7/" + label + " " + c.getUsage()));
            }

        return true;
    }

    private ArrayList<CommandBase> registerCommands() {
        ArrayList<CommandBase> commands = new ArrayList<>();
        commands.add(new ClanCommand(this));
        commands.add(new ClanWhisperCommand(this));
        commands.add(new WarCommand(this));
        commands.add(new SurrenderCommand(this));
        commands.add(new ConfigCommand(this));
        commands.add(new TimeTopCommand(this));
        commands.add(new MineTopCommand(this));
        commands.add(new PlaceTopCommand(this));
        commands.add(new LinkDiscordCommand(this));
        return commands;
    }

    public IEssentials getEssentials() {
        return ess;
    }
    public Economy getVault() {
        return vault;
    }
    public ClanManager getClanManager() {
        return clanManager;
    }
    public WarManager getWarManager() {
        return warManager;
    }
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    public DataManager getDataManager() {
        return dataManager;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
    public DiscordManager getDiscordManager() {
        return discordManager;
    }
    public NametagManager getNametagManager() {
        return nametagManager;
    }
    public GUIManager getGUIManager() {
        return guiManager;
    }
    public Teleport getTeleporter() {
        return teleport;
    }

}
