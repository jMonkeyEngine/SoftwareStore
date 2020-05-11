package com.jayfella.website.core;

import com.jayfella.website.config.external.ServerConfig;

public enum DatabaseType
{
    MYSQL("com.mysql.cj.jdbc.Driver", "mysql://", true),
    POSTGRESQL("org.postgresql.Driver", "postgresql://", true),
    SQLITE("org.sqlite.JDBC", "sqlite:", false);

    private final String driver;
    private final String prefix;
    private final boolean requiresPort;

    DatabaseType(String driver, String prefix, boolean requiresPort) {
        this.driver = driver;
        this.prefix = prefix;
        this.requiresPort = requiresPort;
    }

    public String getDriver()
    {
        return this.driver;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public boolean requiresPort()
    {
        return this.requiresPort;
    }

    public String constructDatabaseUrl() {

        StringBuilder connectionString = new StringBuilder()
                .append("jdbc:")
                .append(getPrefix())
                .append(ServerConfig.getInstance().getDatabaseConfig().getAddress());

        if (requiresPort())
        {
            connectionString
                    .append(":")
                    .append(ServerConfig.getInstance().getDatabaseConfig().getPort())
                    .append("/")
                    .append(ServerConfig.getInstance().getDatabaseConfig().getName());
        }
        else
        {
            connectionString
                    .append(ServerConfig.getInstance().getDatabaseConfig().getName());
        }

        connectionString.append("?serverTimezone=GMT");
        //connectionString.append("&useUnicode=true");
        //connectionString.append("&characterEncoding=utf8");


        return connectionString.toString();
    }

}
