package com.jayfella.website.http.request;

import javax.validation.constraints.NotEmpty;

public class StaffRejectionRequest extends SimplePageRequest {

    @NotEmpty(message = "You must specify a reason for rejection.")
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

}
