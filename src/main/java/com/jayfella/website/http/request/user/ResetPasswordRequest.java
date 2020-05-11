package com.jayfella.website.http.request.user;

import javax.validation.constraints.NotEmpty;

public class ResetPasswordRequest {

    @NotEmpty(message = "You must specify a username.")
    private String username;

    @NotEmpty(message = "You must specify a password.")
    private String password1;

    @NotEmpty(message = "You must specify a password twice.")
    private String password2;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword1() { return password1; }
    public void setPassword1(String password1) { this.password1 = password1; }

    public String getPassword2() { return password2; }
    public void setPassword2(String password2) { this.password2 = password2; }

}
