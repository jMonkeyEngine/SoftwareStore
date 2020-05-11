package com.jayfella.website.config;

import com.jayfella.website.config.external.ServerConfig;
import com.jayfella.website.core.DatabaseType;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EntityScan( basePackages = {"com.jayfella.website"} )
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {

        DatabaseType dbType = DatabaseType.MYSQL;

        return DataSourceBuilder
                .create()
                .username(ServerConfig.getInstance().getDatabaseConfig().getUsername())
                .password(ServerConfig.getInstance().getDatabaseConfig().getPassword())
                .url(dbType.constructDatabaseUrl())
                .driverClassName(dbType.getDriver())
                .build();
    }

}
