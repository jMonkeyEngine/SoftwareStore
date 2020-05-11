package com.jayfella.website.config.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConfig {

    private String type = "mysql";
    private String address = "127.0.0.1";
    private int port = 3306;
    private String name;
    private String username;
    private String password;

    DatabaseConfig() {
    }

    @JsonProperty("type")
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @JsonProperty("address")
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @JsonProperty("port")
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("username")
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @JsonProperty("password")
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
