package com.coco.plotX.configuration;

import com.coco.plotX.configuration.type.MessageConfig;

import java.util.HashMap;
import java.util.Map;

public class FileManager {
    private static Map<Class, Config> configMap = new HashMap<>();

    public void init() {
        var messageConf = new MessageConfig();
        configMap.put(MessageConfig.class, messageConf);
    }
    public void reloadAllConfig() {
        for (Config config : configMap.values()){
            config.reload();
        }
    }

    public static <T extends Config> T getConfig(Class<T> configClass) {
        return (T) configMap.get(configClass);
    }
}
