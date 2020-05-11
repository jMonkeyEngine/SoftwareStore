package com.jayfella.website.http.request.user;

import javax.validation.constraints.NotEmpty;

public class ChangeDetailsRequest {

    @NotEmpty(message = "You must specify a change type.")
    private String type;

    @NotEmpty(message = "You must specify a value.")
    private String value;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

}
