package com.jayfella.website.http.request.user;

import javax.validation.constraints.NotEmpty;

public class RegisterRequest {

    @NotEmpty(message = "Username required.")
    private String username;

    @NotEmpty(message = "Email required.")
    private String email;

    @NotEmpty(message = "Password required.")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
