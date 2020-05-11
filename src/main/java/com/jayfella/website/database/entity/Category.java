package com.jayfella.website.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayfella.website.database.entity.page.stages.LivePage;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Column(length = 128, nullable = false)
    private String name = "";
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @OneToMany(mappedBy="category", fetch = FetchType.LAZY)
    private List<LivePage> pages;
    @JsonIgnore public List<LivePage> getPages() { return pages; }
    @JsonIgnore public void setPages(List<LivePage> pages) { this.pages = pages; }

    @ManyToOne(targetEntity = Category.class, fetch = FetchType.EAGER)
    private Category parent;
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Category> children;
    @JsonIgnore public List<Category> getChildren() { return children; }
    @JsonIgnore public void setChildren(List<Category> children) { this.children = children; }

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

}
