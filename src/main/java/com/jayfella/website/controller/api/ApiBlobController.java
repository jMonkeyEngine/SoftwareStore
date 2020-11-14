package com.jayfella.website.controller.api;

import com.jayfella.website.database.entity.Category;
import com.jayfella.website.database.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/blob")
public class ApiBlobController {

    @Autowired private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<?> getBlob(ModelMap model, HttpServletRequest request) {

        Map<String, Object> data = new HashMap<>();

        processCategories(data, request);

        return ResponseEntity.ok()
                .body(data);
    }

    private void processCategories(Map<String, Object> data, HttpServletRequest request) {

        String[] categories = request.getParameterValues("category");
        List<Category> found = new ArrayList<>();

        boolean getAll = Arrays.stream(categories).anyMatch(cat -> cat.equalsIgnoreCase("all"));

        if (getAll) {
            found.addAll(categoryRepository.findAll());
        }
        else {
            found.addAll(categoryRepository.findByNameIgnoreCaseIn(categories));
        }

        data.put("categories", found);
    }

}
