/*
 * Copyright 2020 Gabriel Keller
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.gmail.creepycucumber1.hungerclans.gui;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class ColorItem extends GUIItem {

    private ChatColor color;

    public ColorItem(ItemStack item, ChatColor color, String colorName) {
        super(item, colorName);
        this.color = color;
    }

    public ColorItem(ItemStack item, String id){
        super(item, id);
    }

    public ChatColor getColor() {
        return color;
    }
}
