/*
 * Copyright 2020 Gabriel Keller
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.gmail.creepycucumber1.hungerclans.util;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class TextUtil {
    public static String convertColor(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String convertArray(Object[] args){
        return convertArray(args, 0);
    }

    public static String convertArray(Object[] args, int start){
        if(args.length==0) return "";
        StringBuilder sb = new StringBuilder();

        for(int i = start; i < args.length; i++){
            sb.append(args[i].toString());
            sb.append(" ");
        }

        return sb.substring(0, sb.length()-1);
    }

    public static Collection<ChatColor> getGradients(String text, ChatColor color1, ChatColor color2){
        text = text.replaceAll("&k", "");
        text = text.replaceAll("&l", "");
        text = text.replaceAll("&m", "");
        text = text.replaceAll("&n", "");
        text = text.replaceAll("&o", "");

        int length = text.length();
        double rStep = Math.abs((double) (color1.getColor().getRed() - color2.getColor().getRed()) / length);
        double gStep = Math.abs((double) (color1.getColor().getGreen() - color2.getColor().getGreen()) / length);
        double bStep = Math.abs((double) (color1.getColor().getBlue() - color2.getColor().getBlue()) / length);
        if (color1.getColor().getRed() > color2.getColor().getRed()) rStep = -rStep;
        if (color1.getColor().getGreen() > color2.getColor().getGreen()) gStep = -gStep;
        if (color1.getColor().getBlue() > color2.getColor().getBlue()) bStep = -bStep;

        Color currentColor = color1.getColor();
        ArrayList<ChatColor> colors = new ArrayList<>();
        for(int i = 0; i < length-1; i++){
            int red = (int) Math.round(currentColor.getRed() + rStep);
            int green = (int) Math.round(currentColor.getGreen() + gStep);
            int blue = (int) Math.round(currentColor.getBlue() + bStep);
            if (red > 255) red = 255; if (red < 0) red = 0;
            if (green > 255) green = 255; if (green < 0) green = 0;
            if (blue > 255) blue = 255; if (blue < 0) blue = 0;

            currentColor = new Color(red, green, blue);
            colors.add(ChatColor.of(currentColor));
        }

        return colors;
    }
}
