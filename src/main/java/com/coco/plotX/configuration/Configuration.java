package com.coco.plotX.configuration;

import com.coco.plotX.PlotX;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Configuration {
    public final String databaseType;
    public final boolean databaseEnabled;
    public final String host;
    public final int port;
    public final String databaseName;
    public final String username;
    public final String password;
    public final String dataFilePath;

    public Configuration(String configFilePath) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> configData;
        try (FileReader fileReader = new FileReader(configFilePath)) {
            configData = yaml.load(fileReader);
        }

        Map<String, Object> databaseConfig = (Map<String, Object>) configData.get("database");
        Map<String, Object> connectionConfig = (Map<String, Object>) databaseConfig.get("connection");

        this.databaseType = (String) databaseConfig.get("type");
        this.databaseEnabled = (Boolean) databaseConfig.get("enabled");
        this.host = (String) connectionConfig.get("host");
        this.port = ((Number) connectionConfig.get("port")).intValue();
        this.databaseName = (String) connectionConfig.get("name");
        this.username = (String) connectionConfig.get("username");
        this.password = (String) connectionConfig.get("password");

        this.dataFilePath = PlotX.instance.getDataFolder().getPath() + File.separator + "data" + File.separator + "plotdata.yml";
        File dataFile = new File(dataFilePath);
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
    }

    public String getDatabaseType() { return databaseType; }
    public boolean isDatabaseEnabled() { return databaseEnabled; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabaseName() { return databaseName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDataFilePath() { return dataFilePath; }
}
