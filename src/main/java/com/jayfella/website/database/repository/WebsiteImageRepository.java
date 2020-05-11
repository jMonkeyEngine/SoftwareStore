package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.WebsiteImage;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated
public interface WebsiteImageRepository extends JpaRepository<WebsiteImage, String> {

}
