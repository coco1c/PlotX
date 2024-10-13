package com.coco.plotX.configuration.type;

import com.coco.plotX.PlotX;
import com.coco.plotX.configuration.Config;
import com.coco.plotX.plots.Plot;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageConfig extends Config {
    private File file;
    private FileConfiguration config;

    @Override
    public void init() throws IOException, InvalidConfigurationException {
        file = new File(PlotX.ROOT_FOLDER, "messages.yml");

        if (!file.exists()) {
            file.createNewFile();
        }

        config = new YamlConfiguration();
        config.load(file);
    }

    @Override
    public void reload() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create config file: " + e.getMessage());
            }
        }

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Failed to reload config file: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
            reload();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config file: " + e.getMessage());
        }
    }
}
