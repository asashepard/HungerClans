package com.gmail.creepycucumber1.hungerclans.discord;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.awt.*;

public class DiscordManager implements Listener {

    private HungerClans plugin;

    public DiscordManager(HungerClans plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        // Example of using JDA's events
        // We need to wait until DiscordSRV has initialized JDA, thus we're doing this inside DiscordReadyEvent
        DiscordUtil.getJda().addEventListener(new JDAListener(plugin));

        // ... we can also do anything other than listen for events with JDA now,
        plugin.getLogger().info("[HungerClans] Chatting on Discord with " + DiscordUtil.getJda().getUsers().size() + " users!");
        // see https://ci.dv8tion.net/job/JDA/javadoc/ for JDA's javadoc
        // see https://github.com/DV8FromTheWorld/JDA/wiki for JDA's wiki
    }

    public void updateUserRoles(OfflinePlayer player) {
        JDA jda = DiscordUtil.getJda();

        String discordID = plugin.getPlayerManager().getDiscordID(player);
        if(discordID.equals("none")) {
            Bukkit.getLogger().info("[HungerClans: updateUserRoles] Discord account undetected for " + player.getName());
            return;
        }

        Guild guild = jda.getGuildById(plugin.getConfigManager().getConfig().getString("discord.guildID"));
        if(guild == null) {
            Bukkit.getLogger().info("[HungerClans: updateUserRoles] Guild could not be found from config!");
            return;
        }

        Member member = guild.getMemberById(discordID);
        if(member == null) {
            Bukkit.getLogger().info("[HungerClans: updateUserRoles] Discord account " + discordID + " not detected in server. Did the account leave?");
            return;
        }

        plugin.getClanManager().getClansThisStartup().forEach(clanName -> {
            if(plugin.getClanManager().getClan(player).equalsIgnoreCase(clanName)) { //player in clan
                addPermission(player, "hungerclans.clan." + clanName.replaceAll(" ", "-"));
            }
            else { //player not in clan
                removePermission(player, "hungerclans.clan." + clanName.replaceAll(" ", "-"));
            }
        });

        if(player.isOnline()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                updatePermissionsRoles(guild, (Player) player);
            }, 40);
        }

    }

    private void addPermission(OfflinePlayer player, String permission) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission " +
                    "set " + permission + " true");
        }, 5);
    }

    private void removePermission(OfflinePlayer player, String permission) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission " +
                    "set " + permission + " false");
        }, 5);
    }

    private void updatePermissionsRoles(Guild guild, Player p) {

        if(!p.isOnline()) {
            Bukkit.getLogger().info("Player " + p.getName() + " no longer online for HungerClans: updatePermissionsRoles.");
            return;
        }

        plugin.getClanManager().getClansThisStartup().forEach(clanName -> {
            String role = "clan." + clanName.replaceAll(" ","-");
            String permission = "hungerclans." + role;

            if(guild.getRolesByName(role, true).isEmpty()) { //no Discord role exists for clan
                Bukkit.getLogger().info("Clan Discord role " + role + " does not exist. Creating...");

                Color color = Color.getHSBColor(
                        (int) (Math.random() * 360), (int) (Math.random() * 50) + 50, (int) (Math.random() * 50) + 50
                );
                guild.createRole().setName(role).setColor(color).queue();

            }

            String discordID = plugin.getPlayerManager().getDiscordID(p);
            Member member = guild.getMemberById(discordID);
            if(member == null) {
                Bukkit.getLogger().info("Error with Discord link involving " + discordID);
                return;
            }

            if(p.hasPermission(permission)) { //player in clan

                if(!discordID.equals("none")) { //player has linked Discord account

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        if(guild.getMembersWithRoles(guild.getRolesByName(role, true).get(0)).contains(member)) { //player is member of clan role
                            //all is as it should be
                        }
                        else {

                            guild.addRoleToMember(member, guild.getRolesByName(role, true).get(0)).queue();

                        }
                    }, 20);
                }
            }
            else { //player NOT in clan

                if(!discordID.equals("none")) { //player has linked Discord account

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        if(guild.getMembersWithRoles(guild.getRolesByName(role, true).get(0)).contains(member)) { //player is member of clan role

                            guild.removeRoleFromMember(member, guild.getRolesByName(role, true).get(0)).queue();

                        }
                        else {
                            //all is as it should be
                        }
                    }, 20);
                }

            }

        });

    }

}