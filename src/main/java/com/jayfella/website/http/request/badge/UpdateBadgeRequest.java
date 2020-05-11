package com.jayfella.website.http.request.badge;

import javax.validation.constraints.NotNull;

public class UpdateBadgeRequest extends CreateBadgeRequest {

    private long id;

    @NotNull(message = "id is empty.")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

}
