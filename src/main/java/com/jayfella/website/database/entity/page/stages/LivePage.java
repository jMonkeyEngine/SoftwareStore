package com.jayfella.website.database.entity.page.stages;

import com.jayfella.website.database.entity.Category;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.page.embedded.SoftwareRating;
import com.jayfella.website.database.repository.CategoryRepository;
import com.jayfella.website.service.ImageService;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.IOException;

@Entity
@Table(name = "LivePages")
public class LivePage extends StorePage {

    public LivePage() {
    }

    @Embedded
    private SoftwareRating rating = new SoftwareRating();
    public SoftwareRating getRating() { return this.rating; }
    public void setRating(SoftwareRating rating) { this.rating = rating; }

    @ManyToOne
    private Category category;
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public LivePage(PageDraft pageDraft, ImageService imageService, CategoryRepository categoryRepository) throws IOException {
        pageDraft.copyTo(this, imageService);

        Category category = categoryRepository.findById(pageDraft.getCategoryId()).orElse(null);
        setCategory(category);
    }

    public void updateFrom(PageAmendment amendment, ImageService imageService, CategoryRepository categoryRepository) throws IOException {
        amendment.copyTo(this, imageService);

        Category category = categoryRepository.findById(amendment.getCategoryId()).orElse(null);
        setCategory(category);
    }

}
