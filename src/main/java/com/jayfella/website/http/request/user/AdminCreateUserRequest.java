package com.jayfella.website.http.request.user;

public class AdminCreateUserRequest extends RegisterRequest {

    boolean sendEmail;

    public boolean isSendEmail() { return sendEmail; }
    public void setSendEmail(boolean sendEmail) { this.sendEmail = sendEmail; }

}
