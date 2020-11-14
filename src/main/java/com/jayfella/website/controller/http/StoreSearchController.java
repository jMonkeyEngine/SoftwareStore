package com.jayfella.website.controller.http;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/search")
public class StoreSearchController {

    @GetMapping
    public String get(Model model) {

        model.addAttribute("categoryId", "-1");
        model.addAttribute("secondary", "title");
        model.addAttribute("term", "");

        model.addAttribute("searchOnLoad", false);

        return "/search/index.html";
    }

    @GetMapping("/{categoryId}")
    public String getWithCategory(Model model,
                                              @PathVariable("categoryId") int categoryId) {

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("secondary", "title");
        model.addAttribute("term", "");

        model.addAttribute("searchOnLoad", true);

        return "/search/index.html";
    }

    @GetMapping("/{categoryId}/{secondary}/{term}")
    public String getWithCategoryAndSecondary(Model model,
                                  @PathVariable("categoryId") int categoryId,
                                  @PathVariable(value = "secondary") String secondary,
                                  @PathVariable(value = "term") String term) {

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("secondary", secondary);
        model.addAttribute("term", term == null ? "" : term);

        model.addAttribute("searchOnLoad", true);

        return "/search/index.html";
    }

}
