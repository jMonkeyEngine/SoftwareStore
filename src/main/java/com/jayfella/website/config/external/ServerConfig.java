package com.jayfella.website.config.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String siteName = "jMonkeyEngine Store";
    private String siteHostName="store.jmonkeyengine.org";
    private String siteScheme="https";
    private String email="noreply@jmonkeyengine.org";


    private String smtpUser="";
    private String smtpPassword="";
    private int smptPort=587;
    private String smptHost="";


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

    public ServerConfig() {
    }

    @JsonProperty("smtp-user")
    public String getSmtpUser() { return smtpUser; }
    public void setSmtpUser(String smtpUser) { this.smtpUser = smtpUser; }

    @JsonProperty("smtp-password")
    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }

    @JsonProperty("smtp-port")
    public int getSmtpPort() { return smptPort; }
    public void setSmtpPort(int smptPort) { this.smptPort = smptPort; }

    @JsonProperty("smtp-host")
    public String getSmtpHost() { return smptHost; }
    public void setSmtpHost(String smptHost) { this.smptHost = smptHost; }

    @JsonProperty("port")
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    @JsonProperty("site-name")       
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    @JsonProperty("site-hostname")   
    public String getSiteHostName() { return siteHostName; }
    public void setSiteHostName(String siteHostName) { this.siteHostName = siteHostName; }

    @JsonProperty("site-scheme")  
    public String getSiteScheme() { return siteScheme; }
    public void setSiteScheme(String siteScheme) { this.siteScheme = siteScheme; }

    @JsonIgnore
    public String getFullUrl() {
        return ServerConfig.getInstance().getSiteScheme() + "://" + ServerConfig.getInstance().getSiteHostName();
    }




    @JsonProperty("email")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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
