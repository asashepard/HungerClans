/*
 * Copyright 2020 Gabriel Keller
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.gmail.creepycucumber1.hungerclans.util;

import org.bukkit.ChatColor;

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

    public static String colorToStringCode(ChatColor color){
        if(color == ChatColor.GRAY) return "&7";

        if (ChatColor.DARK_GRAY.equals(color)) {
            return "&8";
        } else if (ChatColor.AQUA.equals(color)) {
            return "&b";
        } else if (ChatColor.LIGHT_PURPLE.equals(color)) {
            return "&d";
        } else if (ChatColor.YELLOW.equals(color)) {
            return "&e";
        } else if (ChatColor.GOLD.equals(color)) {
            return "&6";
        } else if (ChatColor.WHITE.equals(color)) {
            return "&f";
        } else if (ChatColor.DARK_AQUA.equals(color)) {
            return "&3";
        } else if (ChatColor.DARK_PURPLE.equals(color)) {
            return "&5";
        } else if (ChatColor.BLUE.equals(color)) {
            return "&9";
        } else if (ChatColor.DARK_GREEN.equals(color)) {
            return "&2";
        } else if (ChatColor.GREEN.equals(color)) {
            return "&a";
        } else if (ChatColor.RED.equals(color)) {
            return "&c";
        } else if (ChatColor.BLACK.equals(color)) {
            return "&0";
        }
        return "&7";
    }

    public static ChatColor colorFromString(String s){
        if(s==null) return ChatColor.GRAY;

        switch(s){
            case "gray":
                return ChatColor.DARK_GRAY;
            case "green":
                return ChatColor.GREEN;
            case "yellow":
                return ChatColor.YELLOW;
            case "aqua":
                return ChatColor.AQUA;
            case "magenta":
                return ChatColor.LIGHT_PURPLE;
            case "orange":
                return ChatColor.GOLD;
            case "white":
                return ChatColor.WHITE;
            case "cyan":
                return ChatColor.DARK_AQUA;
            case "purple":
                return ChatColor.DARK_PURPLE;
            case "blue":
                return ChatColor.BLUE;
            case "darkGreen":
                return ChatColor.DARK_GREEN;
            case "red":
                return ChatColor.RED;
            case "black":
                return ChatColor.BLACK;
            default:
                return ChatColor.GRAY;
        }

    }

}
