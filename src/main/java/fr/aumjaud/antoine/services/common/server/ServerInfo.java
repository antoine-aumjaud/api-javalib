package fr.aumjaud.antoine.services.common.server;

import java.util.Properties;

public class ServerInfo {
    private final String name, version, buildDate;

    public ServerInfo(Properties commonProperties) {
        this.name = commonProperties.getProperty("application.name");
        this.version = commonProperties.getProperty("application.version");
        this.buildDate = commonProperties.getProperty("build.date");
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getBuildDate() {
        return buildDate;
    }
}