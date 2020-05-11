package com.jayfella.website.http.request.category;

public class SimpleCategoryRequest {

    private int id;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private int parentId = -1;
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }

}
