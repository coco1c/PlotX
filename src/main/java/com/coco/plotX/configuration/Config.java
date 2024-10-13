package com.coco.plotX.configuration;

import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

public abstract class Config {
    public abstract void init() throws IOException, InvalidConfigurationException;
    public abstract void reload();
}
