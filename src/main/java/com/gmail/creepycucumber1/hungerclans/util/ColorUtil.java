/*
 * Copyright 2020 Gabriel Keller
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.gmail.creepycucumber1.hungerclans.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class ColorUtil {
    public static String colorToString(ChatColor color) {
        if (color == ChatColor.GRAY) return null;

        if (ChatColor.DARK_GRAY.equals(color)) {
            return "gray";
        } else if (ChatColor.AQUA.equals(color)) {
            return "aqua";
        } else if (ChatColor.LIGHT_PURPLE.equals(color)) {
            return "magenta";
        } else if (ChatColor.GOLD.equals(color)) {
            return "orange";
        } else if (ChatColor.WHITE.equals(color)) {
            return "white";
        } else if (ChatColor.DARK_AQUA.equals(color)) {
            return "cyan";
        } else if (ChatColor.DARK_PURPLE.equals(color)) {
            return "purple";
        } else if (ChatColor.BLUE.equals(color)) {
            return "blue";
        } else if (ChatColor.DARK_GREEN.equals(color)) {
            return "darkGreen";
        } else if (ChatColor.GREEN.equals(color)) {
            return "green";
        } else if (ChatColor.RED.equals(color)) {
            return "red";
        } else if (ChatColor.BLACK.equals(color)) {
            return "black";
        }
        return "lightGray";
    }

    public static ItemStack colorToGlass(net.md_5.bungee.api.ChatColor color) {
        if (net.md_5.bungee.api.ChatColor.DARK_GRAY.equals(color)) {
            return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.AQUA.equals(color)) {
            return new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.LIGHT_PURPLE.equals(color)) {
            return new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.GOLD.equals(color)) {
            return new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.YELLOW.equals(color)) {
            return new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.WHITE.equals(color)) {
            return new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.DARK_AQUA.equals(color)) {
            return new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.DARK_PURPLE.equals(color)) {
            return new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.BLUE.equals(color)) {
            return new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.DARK_GREEN.equals(color)) {
            return new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.GREEN.equals(color)) {
            return new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.RED.equals(color)) {
            return new ItemStack(Material.RED_STAINED_GLASS_PANE);
        } else if (net.md_5.bungee.api.ChatColor.BLACK.equals(color)) {
            return new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        }
        return new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    }

    public static Color toAwtColor(ChatColor color) {
        if (ChatColor.DARK_GRAY.equals(color)) {
            return Color.DARK_GRAY;
        } else if (ChatColor.AQUA.equals(color)) {
            return Color.CYAN;
        } else if (ChatColor.LIGHT_PURPLE.equals(color)) {
            return Color.MAGENTA;
        } else if (ChatColor.GOLD.equals(color)) {
            return Color.ORANGE;
        } else if (ChatColor.YELLOW.equals(color)) {
            return Color.YELLOW;
        } else if (ChatColor.WHITE.equals(color)) {
            return Color.WHITE;
        } else if (ChatColor.DARK_AQUA.equals(color)) {
            return Color.getHSBColor(192, 100, 46);
        } else if (ChatColor.DARK_PURPLE.equals(color)) {
            return Color.getHSBColor(295, 100, 46);
        } else if (ChatColor.BLUE.equals(color)) {
            return Color.BLUE;
        } else if (ChatColor.DARK_GREEN.equals(color)) {
            return Color.getHSBColor(115, 100, 51);
        } else if (ChatColor.GREEN.equals(color)) {
            return Color.GREEN;
        } else if (ChatColor.RED.equals(color)) {
            return Color.RED;
        } else if (ChatColor.BLACK.equals(color)) {
            return Color.BLACK;
        }
        return Color.GRAY;
    }

    public static net.md_5.bungee.api.ChatColor colorFromString(String s){
        if(s==null) return net.md_5.bungee.api.ChatColor.GRAY;

        if(s.startsWith("#")){
            return net.md_5.bungee.api.ChatColor.of(s);
        }
        else if(s.equals("")){
            return net.md_5.bungee.api.ChatColor.GRAY;
        }
        else{
            switch(s){
                case "lightGray":
                    return net.md_5.bungee.api.ChatColor.GRAY;
                case "gray":
                    return net.md_5.bungee.api.ChatColor.DARK_GRAY;
                case "pink":
                    return net.md_5.bungee.api.ChatColor.of("#fb91ff");
                case "green":
                    return net.md_5.bungee.api.ChatColor.GREEN;
                case "yellow":
                    return net.md_5.bungee.api.ChatColor.YELLOW;
                case "aqua":
                    return net.md_5.bungee.api.ChatColor.AQUA;
                case "magenta":
                    return net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;
                case "orange":
                    return net.md_5.bungee.api.ChatColor.GOLD;
                case "white":
                    return net.md_5.bungee.api.ChatColor.WHITE;
                case "cyan":
                    return net.md_5.bungee.api.ChatColor.DARK_AQUA;
                case "purple":
                    return net.md_5.bungee.api.ChatColor.DARK_PURPLE;
                case "blue":
                    return net.md_5.bungee.api.ChatColor.BLUE;
                case "brown":
                    return net.md_5.bungee.api.ChatColor.of("#8f7257");
                case "darkGreen":
                    return net.md_5.bungee.api.ChatColor.DARK_GREEN;
                case "red":
                    return net.md_5.bungee.api.ChatColor.RED;
                case "black":
                    return net.md_5.bungee.api.ChatColor.BLACK;
                default:
                    return net.md_5.bungee.api.ChatColor.GRAY;
            }
        }
    }

    public static net.md_5.bungee.api.ChatColor getColor(String hex){
        return net.md_5.bungee.api.ChatColor.of(hex);
    }

}
