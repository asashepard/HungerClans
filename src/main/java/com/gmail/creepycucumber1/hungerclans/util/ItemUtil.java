/*
 * Copyright 2020 Gabriel Keller
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.gmail.creepycucumber1.hungerclans.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtil {
    public static ItemStack createItemStack(Material material, String name, String... lore){
        ItemStack item = new ItemStack(material, 1);

        ItemMeta meta = item.getItemMeta();
        if(meta!=null){
            meta.setDisplayName(TextUtil.convertColor(name));
            ArrayList<String> loreList = new ArrayList<>();
            for(String loreStr : lore){
                loreList.add(TextUtil.convertColor(loreStr));
            }
            meta.setLore(loreList);

            item.setItemMeta(meta);
        }

        return item;
    }
}

