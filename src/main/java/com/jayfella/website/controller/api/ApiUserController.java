package com.jayfella.website.controller.api;

import com.jayfella.website.core.AccountValidationType;
import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.entity.user.UserValidation;
import com.jayfella.website.database.repository.SessionRepository;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.database.repository.UserValidationRepository;
import com.jayfella.website.http.request.user.AdminCreateUserRequest;
import com.jayfella.website.http.request.user.NameUpdateRequest;
import com.jayfella.website.http.request.user.UsernameUpdateRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import com.jayfella.website.service.EmailService;
import com.jayfella.website.service.UserService;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@RestController
@RequestMapping(path = "/api/user/")
public class ApiUserController {

    @Autowired private SessionRepository sessionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserValidationRepository userValidationRepository;

    @Autowired private UserService userService;
    @Autowired private EmailService emailService;

    @GetMapping()
    public User getUser(ModelMap model) {
        return (User) model.get(KEY_USER);
    }

    @GetMapping("/{username}")
    public User getUser(ModelMap model, @PathVariable("username") String username) {
        return userRepository.findByUsernameIgnoreCase(username).orElse(null);
    }

    @GetMapping("/id/{userId}")
    public User getUser(ModelMap model, @PathVariable("userId") long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @GetMapping("/since/{timeLength}")
    public ResponseEntity getUsersSince(ModelMap model, @PathVariable("timeLength") long since) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        Iterable<User> users = userRepository.findByRegisterDateGreaterThan(new Date(since));

        return ResponseEntity.ok()
                .body(users);
    }

    @GetMapping("/search/{searchTerm}")
    public ResponseEntity getUsers(ModelMap model, @PathVariable("searchTerm") String searchTerm) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        Iterable<User> users = userRepository.findByUsernameContaining(searchTerm);

        return ResponseEntity.ok()
                .body(users);
    }

    // allow the user to view their email address.
    @GetMapping("/email/")
    public ResponseEntity getMyEmailAddress(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        return ResponseEntity.ok()
                .body(new SimpleApiResponse(user.getEmail()));
    }

    // allows administrators to view users email addresses.
    @GetMapping("/email/{userId}")
    public ResponseEntity getUserEmailAddress(ModelMap model, @PathVariable("userId") long id) {
        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        User searchedUser = userRepository.findById(id).orElse(null);


        if (!(user.equals(searchedUser) || user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        if (searchedUser == null) {
            return ApiResponses.userNotFound(id);
        }

        return ResponseEntity.ok()
                .body(new SimpleApiResponse(searchedUser.getEmail()));
    }

    @PostMapping("/create/")
    public ResponseEntity createUser(ModelMap model,
                                     @ModelAttribute @Valid AdminCreateUserRequest createUserRequest,
                                     BindingResult bindingResult) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!user.isAdministrator()) {
            return ApiResponses.insufficientPermission();
        }

        List<String> errors;

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        errors = userService.isValidDetails(createUserRequest);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Unable to create user.", errors));
        }

        User newUser = userService.createUser(createUserRequest);

        if (createUserRequest.isSendEmail()) {

            // create validation requirement
            UserValidation userValidation = new UserValidation(newUser.getId(), AccountValidationType.Account, "");
            userValidationRepository.save(userValidation);

            // send validation email.
            try {
                emailService.sendRegistrationEmail(newUser, userValidation);
            } catch (MessagingException e) {
                // @TODO: report this to the administrator group.
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok()
                .body(newUser);
    }

    @PutMapping("/username/")
    public ResponseEntity updateUsername(ModelMap model, @ModelAttribute @Valid UsernameUpdateRequest updateRequest) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (StringUtils.equals(user.getUsername(), updateRequest.getUsername())) {
            return ApiResponses.noChangesDetected();
        }

        user.setUsername(updateRequest.getUsername());
        userRepository.save(user);

        return new ResponseEntity<>(new SimpleApiResponse("Username updated successfully."), HttpStatus.OK);
    }

    @PutMapping("/name/")
    public ResponseEntity updateName(ModelMap model, @ModelAttribute @Valid NameUpdateRequest updateRequest) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (StringUtils.equals(user.getName(), updateRequest.getName())) {
            return ApiResponses.noChangesDetected();
        }

        user.setName(updateRequest.getName());
        userRepository.save(user);

        return new ResponseEntity<>(new SimpleApiResponse("Name updated successfully."), HttpStatus.OK);
    }



}
