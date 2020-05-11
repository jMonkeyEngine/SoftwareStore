package com.jayfella.website.http.request;

import javax.validation.constraints.NotEmpty;

public class SimplePageRequest {

    @NotEmpty(message = "You must specify a page id.")
    private String pageId;

    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }

}
