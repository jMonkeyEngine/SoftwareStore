package com.jayfella.website.http.request.user;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginRequest {

    @NotNull
    @NotEmpty(message = "Username required.")
    private String username;

    @NotNull
    @NotEmpty(message = "Password required.")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
