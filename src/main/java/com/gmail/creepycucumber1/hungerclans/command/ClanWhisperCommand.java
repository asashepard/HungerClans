package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanWhisperCommand extends CommandBase {
    public ClanWhisperCommand(HungerClans plugin) {
        super(plugin, "clanwhisper", "Message only members of your clan", "", "clanmessage", "cw", "cmsg", "ctell");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;
        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.clans")) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cClans have not been enabled."));
            return true;
        }
        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.allowClanChat")) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cClan chat has been muted."));
            return true;
        }
        if(plugin.getEssentials().getUser(player.getUniqueId()).isMuted()) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou are muted."));
            return true;
        }
        if(!plugin.getClanManager().isInClan(player)) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be part of a clan to use this command!"));
            return true;
        }
        if(args.length == 0) {
            player.sendMessage(TextUtil.convertColor("&cUsage: &o/[cw/cmsg] [message]"));
            return true;
        }

        String text = "";
        for(String arg : args) text += arg + " ";
        text = text.substring(0, text.length() - 1);
        while(filter(text)[0] > -2) {
            int[] filter = filter(text);
            Bukkit.getLogger().info(player.getName() + "'s message was blocked: " + text);
            if(filter[0] != -1) text = text.substring(0, filter[0]) + "****" + text.substring(filter[0] + filter[1]);
            else text = "****";
        }
        String message = TextUtil.convertColor("&3[Clan Chat] &b" + (plugin.getClanManager().getRole(player).equals("leader") ? "♠ " : "")
                + player.getName() + " &8» &f" + text);
        TextComponent toSend = new TextComponent(message);
        toSend.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Copy").create()));
        toSend.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text));

        String clan = plugin.getClanManager().getClan(player);
        for(Player p : Bukkit.getOnlinePlayers())
            if(plugin.getClanManager().isInClan(p) &&
                    plugin.getClanManager().getClan(p).equalsIgnoreCase(clan))
                p.sendMessage(toSend);

        ChatColor color = plugin.getClanManager().getColor(clan);
        for(Player admin : Bukkit.getOnlinePlayers())
            if(plugin.getEssentials().getUser(admin.getUniqueId()).isSocialSpyEnabled())
                admin.sendMessage(TextUtil.convertColor("&7[" + color + plugin.getClanManager().getDisplayName(clan) + "&7]" + message.split("]")[1]));

        Bukkit.getLogger().info(message);

        return true;
    }

    private int[] filter(String msg){
        String m = " " + msg.toLowerCase() + " ";
        for(String s : TextUtil.blockedWords){
            if(m.contains(s)){
                if(msg.contains(s)) return new int[]{msg.indexOf(s), s.length()};
                return new int[]{-1, -1};
            }
        }

        return new int[]{-2, -2};
    }
}
