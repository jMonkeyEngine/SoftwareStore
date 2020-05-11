package com.jayfella.website.controller.api.page;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@RestController
@RequestMapping("/api/page/")
public class ApiPageController {

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private UserRepository userRepository;

    @GetMapping("/user/all/")
    public ResponseEntity getAllUserPages(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        Map<String, Iterable<? extends StorePage>> pages = new HashMap<>();

        pages.put("drafts", draftRepository.findByOwner(user));
        pages.put("live", livePageRepository.findByOwner(user));
        pages.put("amendments", amendmentRepository.findByOwner(user));

        return ResponseEntity.ok()
                .body(pages);
    }

    @GetMapping("/user/all/{userId}")
    public ResponseEntity getAllSelectedUserPages(ModelMap model,
                                                  @PathVariable("userId") long userId) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        User selectedUser = userRepository.findById(userId).orElse(null);

        if (selectedUser == null) {
            return ApiResponses.userNotFound(userId);
        }

        Map<String, Iterable<? extends StorePage>> pages = new HashMap<>();

        pages.put("drafts", draftRepository.findByOwner(selectedUser));
        pages.put("live", livePageRepository.findByOwner(selectedUser));
        pages.put("amendments", amendmentRepository.findByOwner(selectedUser));

        return ResponseEntity.ok()
                .body(pages);
    }

}
