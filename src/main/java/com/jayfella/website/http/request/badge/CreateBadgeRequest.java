package com.jayfella.website.http.request.badge;

import javax.validation.constraints.NotEmpty;

public class CreateBadgeRequest {

    @NotEmpty(message = "Name required.")
    private String name;

    @NotEmpty(message = "Description required.")
    private String description;

    @NotEmpty(message = "Icon required.")
    private String icon;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

}
