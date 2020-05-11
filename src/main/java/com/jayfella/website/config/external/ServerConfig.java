package com.jayfella.website.config.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayfella.website.core.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;

public class ServerConfig {

    private static final Logger log = LoggerFactory.getLogger(ServerConfig.class);

    // private static String CONFIG_FILE = "./config/server-config.json";
    private static String CONFIG_FILE = Paths.get(".", "config", "server-config.json").toString();

    private static ServerConfig INSTANCE;

    private int port = 8080;
    private String siteName = "My Website";

    // we use an nginx proxy on the server because it's easier to deal with SSL certificates.
    // this setting only sets the cookie values currently.
    private boolean httpsEnabled = false;

    private DatabaseConfig databaseConfig = new DatabaseConfig();
    private SecurityConfig securityConfig = new SecurityConfig();
    private WebsiteConfig websiteConfig = new WebsiteConfig();

    public static ServerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }

        return INSTANCE;
    }

    private ServerConfig() {
    }

    @JsonProperty("port")
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    @JsonProperty("site-name")
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    @JsonProperty("https-enabled")
    public boolean isHttpsEnabled() { return httpsEnabled; }
    public void setHttpsEnabled(boolean httpsEnabled) { this.httpsEnabled = httpsEnabled; }

    @JsonProperty("database")
    public DatabaseConfig getDatabaseConfig() { return databaseConfig; }
    public void setDatabaseConfig(DatabaseConfig databaseConfig) { this.databaseConfig = databaseConfig; }

    @JsonProperty("security")
    public SecurityConfig getSecurityConfig() { return securityConfig; }
    public void setSecurityConfig(SecurityConfig securityConfig) { this.securityConfig = securityConfig; }

    @JsonProperty("website")
    public WebsiteConfig getWebsiteConfig() { return websiteConfig; }
    public void setWebsiteConfig(WebsiteConfig websiteConfig) { this.websiteConfig = websiteConfig; }

    public void save() {
        JsonMapper.writeFile(CONFIG_FILE, this);
    }

    public static ServerConfig load() {

        File configFile = new File(CONFIG_FILE);
        ServerConfig serverConfig;

        if (!configFile.exists()) {
            serverConfig = new ServerConfig();
            serverConfig.save();

            log.info("A new configuration has been created. You must edit './config/server-config.json' and restart.");
            System.exit(0);
        }
        else {
            serverConfig = JsonMapper.readFile(CONFIG_FILE, ServerConfig.class);
        }

        return serverConfig;
    }




}
