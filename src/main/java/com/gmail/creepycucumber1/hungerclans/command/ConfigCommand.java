package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ConfigCommand extends CommandBase {
    public ConfigCommand(HungerClans plugin) {
        super(plugin, "clanconfig", "Configure the HungerClans plugin", "", "cconfig");
    }

    ArrayList<String> iOptions = new ArrayList<>(plugin.getConfigManager().getConfig().getConfigurationSection("integer").getKeys(false));
    ArrayList<String> bOptions = new ArrayList<>(plugin.getConfigManager().getConfig().getConfigurationSection("boolean").getKeys(false));

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!player.hasPermission("hungerclans.staff")) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou don't have permission to execute this command."));
                return true;
            }
        }

        if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sendConfigHelp(sender);
            return true;
        }
        else if(args.length != 2) {
            sender.sendMessage("Usage: /clanconfig [option] [value]");
            return true;
        }

        String section = (iOptions.contains(args[0]) ? "integer" : (bOptions.contains(args[0]) ? "boolean" : "n/a"));
        if(section.equalsIgnoreCase("n/a")) {
            sender.sendMessage("Unrecognized configuration option.");
            sendConfigHelp(sender);
            return true;
        }
        else if(section.equalsIgnoreCase("integer")) {
            try {
                Integer.parseInt(args[1]);
            } catch (Exception e) {
                sender.sendMessage("Unrecognized datatype.");
                return true;
            }
        }
        else {
            try {
                Boolean.parseBoolean(args[1]);
            } catch (Exception e) {
                sender.sendMessage("Unrecognized datatype.");
                return true;
            }
        }

        ConfigurationSection cfg = plugin.getConfigManager().getConfig().getConfigurationSection(section);
        String before = String.valueOf(cfg.get(args[0]));
        cfg.set(args[0], (section.equalsIgnoreCase("integer") ? Integer.parseInt(args[1]) : Boolean.parseBoolean(args[1])));
        plugin.getConfigManager().saveConfig();
        sender.sendMessage("Successfully set " + args[0] + " to " + plugin.getConfigManager().getConfig().get(section + "." + args[0]) +
                ". It was previously " + before + ".");

        return true;
    }

    public void sendConfigHelp(CommandSender sender) {
        sender.sendMessage("Clan configuration options:");
        String i = "- Integer value - \n";
        String b = "- Boolean value - \n";
        for(String str : iOptions) {
            i += ChatColor.WHITE + " " + str + ": " + ChatColor.YELLOW + plugin.getConfigManager().getConfig().getInt("integer." + str) + "\n";
        }
        for(String str : bOptions) {
            boolean value = plugin.getConfigManager().getConfig().getBoolean("boolean." + str);
            b += ChatColor.WHITE + " " + str + ": " + (value ? ChatColor.DARK_GREEN + "true" : ChatColor.DARK_RED + "false");
        }
        sender.sendMessage(i);
        sender.sendMessage(b);
    }

}
