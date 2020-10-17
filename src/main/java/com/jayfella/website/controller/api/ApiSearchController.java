package com.jayfella.website.controller.api;

import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.repository.CategoryRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.http.response.SimpleApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/search/")
public class ApiSearchController {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private LivePageRepository livePageRepository;

    @GetMapping
    public ResponseEntity<?> searchParam(@RequestParam(required = false, defaultValue = "-1", value = "categoryId") int categoryId,
                                         @RequestParam(required = false, defaultValue = "", value = "title") String title,
                                         @RequestParam(required = false, defaultValue = "", value = "tag") String tag,
                                         @RequestParam(required = false, defaultValue = "", value = "author") String author,
                                         @RequestParam(required = false, defaultValue = "0", value = "page") int pageNum,
                                         @RequestParam(required = false, defaultValue = "newest", value="orderBy") String orderBy,
                                         @RequestParam(required = false, defaultValue = "descending", value = "direction") String direction) {

        // order by
        Map<String, String> allowedProps = new HashMap<>();
        allowedProps.put("title", "details.title");
        allowedProps.put("created", "dateCreated");
        allowedProps.put("updated", "dateUpdated");
        allowedProps.put("rating", "rating.averageRating");

        boolean orderByFound = false;

        for (Map.Entry<String, String> entry : allowedProps.entrySet()) {

            String key = entry.getKey();

            if (orderBy.equalsIgnoreCase(key)) {
                orderBy = entry.getValue();
                orderByFound = true;
                break;
            }
        }

        if (!orderByFound) {
            orderBy = allowedProps.get("title");
        }

        // sort direction
        final Sort.Direction sortDir = direction.equalsIgnoreCase("ascending")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        final int itemsPerPage = 25;
        Pageable pageable = PageRequest.of(pageNum, itemsPerPage, Sort.by(sortDir, orderBy));

        boolean useCategory = categoryId > 0;
        boolean useTitle = !(title==null||title.trim().isEmpty());
        boolean useTag = !(tag==null||tag.trim().isEmpty());
        boolean useAuthor = !(author==null||author.trim().isEmpty());

        Page<LivePage> page = null;

        // category specified
        if (useCategory) {

            // category only
            if (!useTitle && !useTag && !useAuthor) {
                page = livePageRepository.findByCategoryId(categoryId, pageable);
            }

            // category AND x
            else if (useTitle && !useTag && !useAuthor) {
                page = livePageRepository.findByCategoryIdAndDetailsTitleContainingIgnoreCase(categoryId, title, pageable);
            }
            else if (!useTitle && useTag && !useAuthor) {
                page = livePageRepository.findByCategoryIdAndDetailsTagsContainingIgnoreCase(categoryId, tag, pageable);
            }
            else if (!useTitle && !useTag && useAuthor) {
                page = livePageRepository.findByCategoryIdAndOwnerUsernameContainingIgnoreCase(categoryId, author, pageable);
            }

            // category AND x AND y
            else if (useTitle && useTag && !useAuthor) {
                page = livePageRepository.findByCategoryIdAndDetailsTitleContainingIgnoreCaseAndDetailsTagsContainingIgnoreCase(categoryId, title, tag, pageable);
            }
            else if (useTitle && !useTag && useAuthor) {
                page = livePageRepository.findByCategoryIdAndDetailsTitleContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(categoryId, title, author, pageable);
            }
            else if (!useTitle && useTag && useAuthor) {
                page = livePageRepository.findByCategoryIdAndDetailsTagsContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(categoryId, tag, author, pageable);
            }

            // category AND x AND y AND z
            else if (useTitle && useTag && useAuthor) {
                page = livePageRepository.findByCategoryIdAndDetailsTitleContainingIgnoreCaseAndDetailsTagsContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(categoryId, title, tag, author, pageable);
            }
        }

        // no category specified
        else {

            // singular
            if (useTitle && !useTag && !useAuthor) {
                page = livePageRepository.findByDetailsTitleContainingIgnoreCase(title, pageable);
            }
            else if (!useTitle && useTag && !useAuthor) {
                page = livePageRepository.findByDetailsTagsContainingIgnoreCase(tag, pageable);
            }
            else if (!useTitle && !useTag && useAuthor) {
                page = livePageRepository.findByOwnerUsernameContainingIgnoreCase(author, pageable);
            }

            // x AND y
            else if (useTitle && useTag && !useAuthor) {
                page = livePageRepository.findByDetailsTitleContainingIgnoreCaseAndDetailsTagsContainingIgnoreCase(title, tag, pageable);
            }
            else if (useTitle && !useTag && useAuthor) {
                page = livePageRepository.findByDetailsTitleContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(title, author, pageable);
            }
            else if (!useTitle && useTag && useAuthor) {
                page = livePageRepository.findByDetailsTagsContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(tag, author, pageable);
            }

            // x AND y AND z
            else if (useTitle && useTag && useAuthor) {
                page = livePageRepository.findByDetailsTitleContainingIgnoreCaseAndDetailsTagsContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(title, tag, author, pageable);
            }

            else {
                // nothing at all specified.
                page = livePageRepository.findAll(pageable);
            }
        }

        // String debugOut = "Search: Category: %b Title: %b Tags: %b Author: %b";
        // System.out.println(String.format(debugOut, useCategory, useTitle, useTag, useAuthor));

        if (page != null) {
            return ResponseEntity.ok()
                    .body(page);
        }
        else {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Unknown search mix."));
        }

    }


}
