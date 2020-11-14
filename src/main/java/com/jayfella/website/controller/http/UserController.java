package com.jayfella.website.controller.http;

import com.jayfella.website.config.external.ServerConfig;
import com.jayfella.website.core.AccountValidationType;
import com.jayfella.website.core.ServerAdvice;
import com.jayfella.website.core.ServerUtilities;
import com.jayfella.website.core.StoreHtmlFilePaths;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.entity.user.UserValidation;
import com.jayfella.website.database.repository.SessionRepository;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.database.repository.UserValidationRepository;
import com.jayfella.website.http.request.user.LoginRequest;
import com.jayfella.website.http.request.user.RegisterRequest;
import com.jayfella.website.service.EmailService;
import com.jayfella.website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jayfella.website.core.ServerAdvice.KEY_SESSION;
import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@Controller
@RequestMapping(value = "/user/", produces = MediaType.TEXT_HTML_VALUE)
public class UserController {

    @Autowired private UserValidationRepository userValidationRepository;

    @Autowired private UserRepository userRepository;
    @Autowired private SessionRepository sessionRepository;

    @Autowired private EmailService emailService;
    @Autowired private UserService userService;

    @GetMapping()
    public String getIndexPage(HttpServletResponse response, ModelMap model) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            response.sendRedirect(StoreHtmlFilePaths.User.LOGIN.getUrlPath());
            return null;
        }

        /*
        userValidationRepository.findByUserId(user.getId())
                .ifPresent(validation -> model.addAttribute("validation", validation));
         */

        return StoreHtmlFilePaths.User.INDEX.getHtmlFilePath();
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return StoreHtmlFilePaths.User.LOGIN.getHtmlFilePath();
    }

    @PostMapping("/login")
    public String postLogin(HttpServletResponse response, Model model,
                            @ModelAttribute @Valid LoginRequest loginRequest,
                            BindingResult result) throws IOException {

        if (result.hasErrors()) {
            List<String> errors = ServerUtilities.getErrorMessages(result);
            model.addAttribute("error", errors);
            return StoreHtmlFilePaths.User.LOGIN.getHtmlFilePath();
        }

        User user = userRepository.findByUsernameIgnoreCase(loginRequest.getUsername()).orElse(null);

        if (user == null) {
            model.addAttribute("error", new String[] { "Invalid username or password specified." });
            return StoreHtmlFilePaths.User.LOGIN.getHtmlFilePath();
        }

        boolean passwordMatch = userService.passwordsMatch(user, loginRequest.getPassword());

        if (!passwordMatch) {
            model.addAttribute("error", new String[] { "Invalid username or password specified." });
            return StoreHtmlFilePaths.User.LOGIN.getHtmlFilePath();
        }

        Cookie cookie = userService.createSessionCookie(user);
        response.addCookie(cookie);

        response.sendRedirect(StoreHtmlFilePaths.Store.INDEX.getUrlPath());
        return null;
    }

    @PostMapping("/logout")
    public String postLogout(ModelMap model,
                             HttpServletResponse response,
                             @CookieValue(value = KEY_SESSION) String session) throws IOException {

        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user != null) {
            sessionRepository.findBySession(session).ifPresent(s -> sessionRepository.delete(s));
        }

        response.sendRedirect(StoreHtmlFilePaths.Store.INDEX.getUrlPath());
        return null;
    }

    @GetMapping("/register")
    public String getRegisterPage(HttpServletResponse response) throws IOException {

        if (ServerConfig.getInstance().getWebsiteConfig().isRegistrationDisabled()) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Registration is currently disabled.");
            return null;
        }

        return StoreHtmlFilePaths.User.REGISTER.getHtmlFilePath();
    }

    @GetMapping("/registered")
    public String getRegisteredPage() {
        return StoreHtmlFilePaths.User.REGISTERED.getHtmlFilePath();
    }

    @PostMapping("/register")
    public String postRegisterPage(HttpServletResponse response, Model model,
                                   @ModelAttribute @Valid RegisterRequest registerRequest,
                                   BindingResult result) throws IOException {

        if (ServerConfig.getInstance().getWebsiteConfig().isRegistrationDisabled()) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Registration is currently disabled.");
            return null;
        }

        List<String> errors = new ArrayList<>();

        if (result.hasErrors()) {
            errors.addAll(ServerUtilities.getErrorMessages(result));
            model.addAttribute("The following errors occurred:", errors);
            return StoreHtmlFilePaths.User.REGISTER.getHtmlFilePath();
        }

        errors = userService.isValidDetails(registerRequest);

        if (!errors.isEmpty()) {
            model.addAttribute("error", errors);
            return StoreHtmlFilePaths.User.REGISTER.getHtmlFilePath();
        }

        User user = userService.createUser(registerRequest);
        model.addAttribute(KEY_USER, user);

        // create a session
        Cookie cookie = userService.createSessionCookie(user);
        response.addCookie(cookie);

        // create validation requirement
        UserValidation userValidation = new UserValidation(user.getId(), AccountValidationType.Account, "");
        userValidationRepository.save(userValidation);

        // send validation email.
        try {
            emailService.sendRegistrationEmail(user, userValidation);
        } catch (MessagingException e) {
            // @TODO: report this to the administrator group.
            e.printStackTrace();
        }

        response.sendRedirect(StoreHtmlFilePaths.User.REGISTERED.getUrlPath());
        return null;
    }

    @GetMapping("/my-pages")
    public String displayUserAssets(HttpServletResponse response, ModelMap model) throws IOException {

        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null) {
            response.sendRedirect(StoreHtmlFilePaths.User.LOGIN.getUrlPath());
            return null;
        }

        return StoreHtmlFilePaths.User.MY_PAGES.getHtmlFilePath();
    }

    @GetMapping("/profile/{userId}")
    public String getUserProfile(Model model, HttpServletResponse response, @PathVariable("userId")long userId) throws IOException {

        model.addAttribute("requested_user", userId);
        return StoreHtmlFilePaths.User.PROFILE.getHtmlFilePath();
    }

    @GetMapping("/reset-password")
    public String getResetPassword() {
        return "/user/reset-password.html";
    }

    @GetMapping("/reset-password/{validationCode}")
    public String resetPasswordWithCode(HttpServletResponse response, @PathVariable("validationCode") String validationCode) throws IOException {

        UserValidation userValidation = userValidationRepository.findById(validationCode).orElse(null);

        if (userValidation == null || userValidation.getValidationType() != AccountValidationType.Password) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid code specified.");
            return null;
        }

        User user = userRepository.findById(userValidation.getUserId()).orElse(null);

        if (user == null) {
            userValidationRepository.delete(userValidation);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "The specified code was valid but the user no longer exists.");
            return null;
        }

        user.setPassword(userValidation.getValue());
        userRepository.save(user);

        userValidationRepository.delete(userValidation);

        return "/user/password-validated.html";
    }



}
