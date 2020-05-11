package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Set<Category> findAllByIdIn(int[] ids);

    Optional<Category> findByName(String name);
    Optional<Category> findByNameIgnoreCase(String name);

    Iterable<Category> findByNameContainingIgnoreCase(String name);
    Collection<Category> findByNameIgnoreCaseIn(String[] names);

}
