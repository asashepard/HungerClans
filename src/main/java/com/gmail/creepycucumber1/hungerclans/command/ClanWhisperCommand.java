package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ClanWhisperCommand extends CommandBase {
    public ClanWhisperCommand(HungerClans plugin) {
        super(plugin, "clanwhisper", "Message only members of your clan", "", "clanmessage", "cw", "cmsg", "ctell");
    }
    private final List<String> blockedWords = Arrays.asList("nigger", "faggot", "nigga", "burn jews", " fag ");

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&4CLANS &8» &cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;
        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.clans")) {
            player.sendMessage(TextUtil.convertColor("&4CLANS &8» &cClans have not been enabled."));
            return true;
        }
        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.allowClanChat")) {
            player.sendMessage(TextUtil.convertColor("&4CLANS &8» &cClan chat has been muted."));
            return true;
        }
        if(plugin.getEssentials().getUser(player.getUniqueId()).isMuted()) {
            player.sendMessage(TextUtil.convertColor("&4CLANS &8» &cYou are muted."));
            return true;
        }
        if(!plugin.getClanManager().isInClan(player)) {
            player.sendMessage(TextUtil.convertColor("&4CLANS &8» &cYou must be part of a clan to use this command!"));
            return true;
        }
        if(args.length == 0) {
            player.sendMessage(TextUtil.convertColor("&cUsage: &o/[cw/cmsg] [message]"));
            return true;
        }

        String message = "";
        for(int i = 0; i < args.length; i++)
            message += args[i] + " ";
        message = message.substring(0, message.length() - 1);
        message = TextUtil.convertColor("&3[Clan Chat] &b" + player.getName() + " &8» &f" + message);
        String clan = plugin.getClanManager().getClan(player);

        for(Player p : Bukkit.getOnlinePlayers())
            if(plugin.getClanManager().isInClan(p) &&
                    plugin.getClanManager().getClan(p).equalsIgnoreCase(clan))
                p.sendMessage(message);

        String color = ColorUtil.colorToStringCode(plugin.getClanManager().getColor(clan));
        for(Player admin : Bukkit.getOnlinePlayers())
            if(plugin.getEssentials().getUser(admin.getUniqueId()).isSocialSpyEnabled())
                admin.sendMessage(TextUtil.convertColor("&7[" + color + clan + "&7]" + message.split("]")[1]));

        Bukkit.getLogger().info(message);

        return true;
    }

    private boolean filter(String msg){
        msg = " " + msg.toLowerCase() + " ";
        for(String s : blockedWords){
            if(msg.toLowerCase().contains(s)){
                return true;
            }
        }

        return false;
    }
}
