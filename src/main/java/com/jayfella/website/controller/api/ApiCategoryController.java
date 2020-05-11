package com.jayfella.website.controller.api;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.database.entity.Category;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.CategoryRepository;
import com.jayfella.website.http.request.category.SimpleCategoryRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@RestController
@RequestMapping("/api/category/")
public class ApiCategoryController {

    @Autowired private CategoryRepository categoryRepository;

    @PostMapping()
    public ResponseEntity<?> createCategory(ModelMap model,
                                            @ModelAttribute @Valid SimpleCategoryRequest request,
                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        if (request.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("You must provide a name."));
        }

        Category category = new Category(request.getName());

        if (request.getParentId() > 0) {
            Category parent = categoryRepository.findById(request.getParentId()).orElse(null);

            if (parent != null) {
                category.setParent(parent);
            }
            else {
                return ResponseEntity.badRequest()
                        .body(new SimpleApiResponse("Could not find parent with ID: " + request.getParentId()));
            }
        }

        categoryRepository.save(category);

        return ResponseEntity.ok()
                .body(category);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCategory(ModelMap model,
                                            @ModelAttribute  @Valid SimpleCategoryRequest request,
                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        Category category = categoryRepository.findById(request.getId()).orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Category not found."));
        }

        categoryRepository.delete(category);

        return ResponseEntity.ok()
                .body(category);
    }

    @PutMapping
    public ResponseEntity<?> updateCategory(ModelMap model,
                                            @ModelAttribute @Valid SimpleCategoryRequest request,
                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        Category category = categoryRepository.findById(request.getId()).orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Cannot find category with ID: " + request.getId()));
        }

        if (!StringUtils.equals(request.getName(), category.getName())) {
            category.setName(request.getName());
        }

        if (request.getParentId() > 0) {
            Category parent = categoryRepository.findById(request.getParentId()).orElse(null);

            if (parent != null) {
                category.setParent(parent);
            }
            else {
                return ResponseEntity.badRequest()
                        .body(new SimpleApiResponse("Could not find parent with ID: " + request.getParentId()));
            }

            if (isCircularReference(category, parent)) {
                return ResponseEntity.badRequest()
                        .body(new SimpleApiResponse("Circular reference."));
            }
        }

        categoryRepository.save(category);

        return ResponseEntity.ok()
                .body(category);
    }

    // https://stackoverflow.com/a/30958018
    private boolean isCircularReference(Category child, Category parent)
    {
        // $node = $parent;
        Category node = parent;
        while(node != null)
        {
            if(node.getId() == child.getId()) {
                return true;  //Looped back around to the child
            }

            node = node.getParent();
        }
        return false;  //No loops found
    }

}
