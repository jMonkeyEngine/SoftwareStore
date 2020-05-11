package com.jayfella.website.http.request.user.avatar;

import javax.validation.constraints.NotEmpty;

public class ChangeGravatarAvatarRequest {

    @NotEmpty(message = "Empty email hash")
    private String emailHash;

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }
}
