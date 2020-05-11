package com.jayfella.website.http.request.user;

import javax.validation.constraints.NotEmpty;

public class ValidationRequest {

    @NotEmpty(message = "You must specify a validation code.")
    private String code;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

}
