package com.jayfella.website.controller.api;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.database.entity.Badge;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.BadgeRepository;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.http.request.badge.CreateBadgeRequest;
import com.jayfella.website.http.request.badge.DeleteBadgeRequest;
import com.jayfella.website.http.request.badge.UpdateBadgeRequest;
import com.jayfella.website.http.request.badge.UserBadgeRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

/**
 * CRUD Repository for Badges
 */
@RestController
@RequestMapping("/api/badges/")
public class ApiBadgeController {

    @Autowired private BadgeRepository badgeRepository;
    @Autowired private UserRepository userRepository;

    @PostMapping
    public ResponseEntity create(ModelMap model,
                                 @ModelAttribute @Valid CreateBadgeRequest createBadgeRequest,
                                 BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!user.isAdministrator()) {
            return ApiResponses.insufficientPermission();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        Badge badge = new Badge(createBadgeRequest);
        badgeRepository.save(badge);

        return ResponseEntity.ok()
                .body(badge);
    }

    @GetMapping
    public ResponseEntity read() {

        Iterable<Badge> badges = badgeRepository.findAll();
        return new ResponseEntity<>(badges, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity update(ModelMap model,
                                 @ModelAttribute @Valid UpdateBadgeRequest updateBadgeRequest,
                                 BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!user.isAdministrator()) {
            return ApiResponses.insufficientPermission();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        Badge badge = badgeRepository.findById(updateBadgeRequest.getId()).orElse(null);

        if (badge == null) {
            return new ResponseEntity<>(new SimpleApiResponse("Cannot locate badge with id: " + updateBadgeRequest.getId()), HttpStatus.BAD_REQUEST);
        }

        badge.setName(updateBadgeRequest.getName());
        badge.setDescription(updateBadgeRequest.getDescription());
        badge.setIcon(updateBadgeRequest.getIcon());

        badgeRepository.save(badge);

        return ResponseEntity.ok()
                .body(badge);
    }

    @DeleteMapping
    public ResponseEntity delete(ModelMap model,
                                 @ModelAttribute @Valid DeleteBadgeRequest deleteBadgeRequest,
                                 BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!user.isAdministrator()) {
            return ApiResponses.insufficientPermission();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        Badge badge = badgeRepository.findById(deleteBadgeRequest.getBadgeId()).orElse(null);

        if (badge == null) {
            return new ResponseEntity<>(new SimpleApiResponse("Cannot find badge with id: " + deleteBadgeRequest.getBadgeId()), HttpStatus.BAD_REQUEST);
        }

        badgeRepository.delete(badge);

        return ResponseEntity.ok()
                .body(new SimpleApiResponse("Badge deleted."));
    }

    @PostMapping("/grant/")
    public ResponseEntity grantBadge(ModelMap model,
                                     @ModelAttribute @Valid UserBadgeRequest grantRequest,
                                     BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!user.isAdministrator()) {
            return ApiResponses.insufficientPermission();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        Badge badge = badgeRepository.findById(grantRequest.getBadgeId()).orElse(null);

        if (badge == null) {
            return new ResponseEntity<>(new SimpleApiResponse("Cannot find badge with id: " + grantRequest.getBadgeId()), HttpStatus.BAD_REQUEST);
        }

        User userToGrant = userRepository.findById(grantRequest.getUserId()).orElse(null);

        if (userToGrant == null) {
            return ApiResponses.userNotFound(grantRequest.getUserId());
        }

        if (userToGrant.getBadges() == null) {
            userToGrant.setBadges(new ArrayList<>());
        }

        userToGrant.getBadges().add(badge);
        userRepository.save(userToGrant);

        return ResponseEntity.ok()
                .body(badge);
    }

    @PostMapping("/revoke/")
    public ResponseEntity revokeBadge(ModelMap model,
                                      @ModelAttribute @Valid UserBadgeRequest revokeRequest,
                                      BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!user.isAdministrator()) {
            return ApiResponses.insufficientPermission();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        Badge badge = badgeRepository.findById(revokeRequest.getBadgeId()).orElse(null);

        if (badge == null) {
            return new ResponseEntity<>(new SimpleApiResponse("Cannot find badge with id: " + revokeRequest.getBadgeId()), HttpStatus.BAD_REQUEST);
        }

        User userToRevoke = userRepository.findById(revokeRequest.getUserId()).orElse(null);

        if (userToRevoke == null) {
            return ApiResponses.userNotFound(revokeRequest.getUserId());
        }

        if (userToRevoke.getBadges() == null || !userToRevoke.getBadges().contains(badge)) {
            return new ResponseEntity<>(new SimpleApiResponse("User does not have badge: " + badge.getName()), HttpStatus.BAD_REQUEST);
        }

        userToRevoke.getBadges().remove(badge);
        userRepository.save(userToRevoke);

        return ResponseEntity.ok()
                .body(badge);

    }
}
