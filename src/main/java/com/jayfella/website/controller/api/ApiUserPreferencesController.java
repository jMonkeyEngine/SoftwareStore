package com.jayfella.website.controller.api;

import com.jayfella.website.database.repository.SessionRepository;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.database.repository.UserValidationRepository;
import com.jayfella.website.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/user/preferences/")
public class ApiUserPreferencesController {

    @Autowired private UserRepository userRepository;
    @Autowired private SessionRepository sessionRepository;
    @Autowired private UserValidationRepository userValidationRepository;

    @Autowired private EmailService emailService;

}
