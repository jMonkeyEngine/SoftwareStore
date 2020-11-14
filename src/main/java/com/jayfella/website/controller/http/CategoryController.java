package com.jayfella.website.controller.http;

import com.jayfella.website.database.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping(path = "/category", produces = MediaType.TEXT_HTML_VALUE)
public class CategoryController {

    @Autowired private CategoryRepository categoryRepository;

    @RequestMapping("/{categoryName}")
    public String listCategory(ModelMap model, @PathVariable("categoryName") String categoryName) throws IOException {
        model.put("category", categoryName);
        return "/category/index.html";
    }

}
