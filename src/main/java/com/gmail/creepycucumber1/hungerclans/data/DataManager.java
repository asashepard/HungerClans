/*
 * Copyright 2020 Gabriel Keller
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.gmail.creepycucumber1.hungerclans.data;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {

    private static final String CONFIG_NAME = "clandata.yml";
    private HungerClans plugin;
    private FileConfiguration config;
    private File configFile;

    public DataManager(HungerClans plugin){
        this.plugin = plugin;
        saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig(){
        if(this.configFile == null){
            this.configFile = new File(this.plugin.getDataFolder(), CONFIG_NAME);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource(CONFIG_NAME);

        if(defaultStream!=null){
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig(){
        if(config==null) reloadConfig();

        return config;
    }

    public void saveConfig(){
        if(config == null || configFile == null) return;

        try{
            getConfig().save(configFile);
        }
        catch(IOException ex){
            plugin.getLogger().log(Level.SEVERE, "Could not save to config " + configFile, ex);
        }
    }

    public void saveDefaultConfig(){
        if(configFile==null){
            configFile = new File(plugin.getDataFolder(), CONFIG_NAME);
        }

        if(!configFile.exists()){
            plugin.saveResource(CONFIG_NAME, false);
        }
    }

}