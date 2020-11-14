package com.jayfella.website.controller.api;

import com.jayfella.website.core.AccountValidationType;
import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.entity.user.UserValidation;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.database.repository.UserValidationRepository;
import com.jayfella.website.http.request.user.ChangeDetailsRequest;
import com.jayfella.website.http.request.user.ResetPasswordRequest;
import com.jayfella.website.http.request.user.ValidationRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import com.jayfella.website.service.EmailService;
import com.jayfella.website.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

/**
 * Validates new accounts, email changes and password changes.
 */

@RestController
@RequestMapping("/api/validate")
public class ApiValidationController {

    @Autowired private UserValidationRepository userValidationRepository;
    @Autowired private UserRepository userRepository;

    @Autowired private EmailService emailService;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity getValidation(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        UserValidation validation = userValidationRepository.findByUserId(user.getId()).orElse(null);

        return ResponseEntity.ok()
                .body(validation);
    }

    // validates a given code
    @PostMapping
    public ResponseEntity validateCode(@ModelAttribute @Valid ValidationRequest validationRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        UserValidation userValidation = userValidationRepository.findById(validationRequest.getCode()).orElse(null);

        if (userValidation != null) {

            User user = userRepository.findById(userValidation.getUserId()).orElse(null);

            if (user == null) {
                userValidationRepository.delete(userValidation);
                return ApiResponses.userNotFound(userValidation.getUserId());
            }

            switch (userValidation.getValidationType()) {
                case Email:
                    user.setEmail(userValidation.getValue());
                    userRepository.save(user);
                    break;

                case Password:
                    user.setPassword(userValidation.getValue());
                    userRepository.save(user);
                    break;
                case Account:
                default:
            }

            userValidationRepository.delete(userValidation);
            return ResponseEntity.ok()
                    .body(new SimpleApiResponse("Validation successful."));
        }

        return ResponseEntity.badRequest()
                .body(new SimpleApiResponse("Invalid validation code."));
    }

    // sends a new email validation email to the user.
    @PostMapping("/resend")
    public ResponseEntity sendNewEmailValidation(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        UserValidation validation = userValidationRepository.findByUserId(user.getId()).orElse(null);

        // if there is no validation request pending, we can't send an email.
        if (validation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SimpleApiResponse("You have no validation requests pending."));
        }

        // the validation request exists so we check how long ago they requested it.
        // this avoids people spamming emails from our server.

        if (isTooQuick(validation)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SimpleApiResponse("You must wait at least 5 minutes between requesting validation emails."));
        }

        // delete the old request and generate a new one.
        UserValidation newValidation = new UserValidation(user.getId(), validation.getValidationType(), validation.getValue());
        userValidationRepository.delete(validation);
        userValidationRepository.save(newValidation);

        try {
            emailService.sendAccountValidationEmail(user, newValidation);
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .body(new SimpleApiResponse("An email has been sent to your email address."));
    }

    // USER requests a change in their details (email, password).
    @PostMapping("/details")
    public ResponseEntity changeDetails(ModelMap model, @ModelAttribute @Valid ChangeDetailsRequest changeDetailsRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        UserValidation userValidation = userValidationRepository.findByUserId(user.getId()).orElse(null);

        if (userValidation != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SimpleApiResponse("You already have a validation pending."));
        }

        AccountValidationType validation = AccountValidationType.fromString(changeDetailsRequest.getType());

        if (validation == null) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Unknown type: " + changeDetailsRequest.getType()));
        }

        switch (validation) {
            case Email:

                // preliminary checks
                // - is it a valid email?
                // - has the email changed?
                // - does someone else own this email address?

                // we're opting for apache commons over spring @Email because apparently spring allows all kinds of bad inputs.
                boolean validEmail = EmailValidator.getInstance(false).isValid(changeDetailsRequest.getValue());
                if (!validEmail) {
                    return ResponseEntity.badRequest()
                            .body(new SimpleApiResponse("Invalid email format specified."));
                }

                if (user.getEmail().equalsIgnoreCase(changeDetailsRequest.getValue())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new SimpleApiResponse("The email address is the same as the stored address."));
                }

                User emailDuplicate = userRepository.findByEmailIgnoreCase(changeDetailsRequest.getValue()).orElse(null);

                if (emailDuplicate != null && emailDuplicate.getId() != user.getId()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new SimpleApiResponse("An account with this email already exists."));
                }

                userValidation = new UserValidation(user.getId(), AccountValidationType.Email, changeDetailsRequest.getValue());
                userValidationRepository.save(userValidation);
                break;

            case Password:

                // preliminary checks
                // - is it long enough?
                // - is it the same?

                if (changeDetailsRequest.getValue().length() < UserService.PASSWORD_MIN_LENGTH) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new SimpleApiResponse("Your password must be at least " + UserService.PASSWORD_MIN_LENGTH + " characters long."));
                }

                if (userService.passwordsMatch(user, changeDetailsRequest.getValue())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new SimpleApiResponse("The given password is identical to the stored password."));
                }

                String hashAndSalt = userService.createNewHashAndSalt(changeDetailsRequest.getValue());

                userValidation = new UserValidation(user.getId(), AccountValidationType.Password, hashAndSalt);
                userValidationRepository.save(userValidation);

                break;
            case Account:
            default:
                return ResponseEntity.badRequest()
                        .body(new SimpleApiResponse("Unsupported type: " + changeDetailsRequest.getType()));
        }

        try {
            emailService.sendAccountValidationEmail(user, userValidation);
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .body(new SimpleApiResponse("Please check your email for a validation code."));
    }

    @PostMapping("/cancel")
    public ResponseEntity cancelValidationRequest(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        UserValidation userValidation = userValidationRepository.findByUserId(user.getId()).orElse(null);

        if (userValidation == null) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("There are no validation requests to cancel."));
        }

        if (userValidation.getValidationType() == AccountValidationType.Account) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SimpleApiResponse("You cannot cancel an account validation request."));
        }

        userValidationRepository.delete(userValidation);

        return ResponseEntity.ok()
                .body(new SimpleApiResponse("Validation request deleted."));
    }

    // USER requests a new password (locked out)
    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(HttpServletRequest request,
                                        @ModelAttribute @Valid ResetPasswordRequest resetRequest,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        if (!StringUtils.equals(resetRequest.getPassword1(), resetRequest.getPassword2())) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Both passwords must match."));
        }

        if (resetRequest.getPassword1().length() < UserService.PASSWORD_MIN_LENGTH) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SimpleApiResponse("Your password must be at least " + UserService.PASSWORD_MIN_LENGTH + " characters long."));
        }

        // check if this is a valid username
        User user = userRepository.findByUsernameIgnoreCase(resetRequest.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Username not found."));
        }

        if (userService.passwordsMatch(user, resetRequest.getPassword1())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleApiResponse("The given password is identical to the stored password."));
        }

        // check if one already exists, and if it has already been sent < 5 minutes ago.

        UserValidation userValidation = userValidationRepository.findByUserId(user.getId()).orElse(null);

        if (userValidation != null) {
            if (isTooQuick(userValidation)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new SimpleApiResponse("You must wait at least 5 minutes between requesting validation emails."));
            }

            userValidationRepository.delete(userValidation);
        }

        String hashAndSalt = userService.createNewHashAndSalt(resetRequest.getPassword1());
        userValidation = new UserValidation(user.getId(), AccountValidationType.Password, hashAndSalt);
        userValidationRepository.save(userValidation);

        try {
            // emailService.sendAccountValidationEmail(user, userValidation);

            String ipAddress = getClientIp(request);

            if (ipAddress==null||ipAddress.trim().isEmpty()) {
                ipAddress = "UNKNOWN";
            }

            emailService.sendResetPasswordEmail(user, userValidation.getId(), ipAddress);
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .body(new SimpleApiResponse("An email has been sent to your email address."));
    }

    private boolean isTooQuick(UserValidation userValidation) {
        long minTimeBetweenRequests = 300000; // 5 minutes

        long timeNow = System.currentTimeMillis();
        long lastReq = userValidation.getCreationDate().getTime();

        long timeDiff = timeNow - lastReq;

        return (timeDiff < minTimeBetweenRequests);
    }

    // https://www.mkyong.com/spring-mvc/spring-mvc-how-to-get-client-ip-address/
    private String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

}
