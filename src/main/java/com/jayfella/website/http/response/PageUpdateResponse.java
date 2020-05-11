package com.jayfella.website.http.response;

import com.jayfella.website.database.entity.page.StorePage;

import java.util.List;

public class PageUpdateResponse {

    private StorePage page;
    private List<String> areasUpdated;

    public PageUpdateResponse(StorePage page, List<String> areasUpdated) {
        this.page = page;
        this.areasUpdated = areasUpdated;
    }

    public List<String> getAreasUpdated() { return areasUpdated; }
    public void setAreasUpdated(List<String> areasUpdated) { this.areasUpdated = areasUpdated; }

    public StorePage getPage() { return page; }
    public void setPage(StorePage page) { this.page = page; }

}
