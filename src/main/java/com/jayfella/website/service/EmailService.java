package com.jayfella.website.service;

import com.jayfella.website.config.external.ServerConfig;
import com.jayfella.website.core.AccountValidationType;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.entity.user.UserValidation;
import com.jayfella.website.database.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private JavaMailSender javaMailSender;
    private TemplateEngine templateEngine;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendRegistrationEmail(User user, UserValidation validation) throws MessagingException, UnsupportedEncodingException {

        if (!ServerConfig.getInstance().getWebsiteConfig().isEmailEnabled()) {
            return;
        }

        final Context ctx = new Context(Locale.UK);

        // Prepare the evaluation context
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("validationCode", validation.getId());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject("JmonkeyStore");
        message.setFrom(new InternetAddress("noreply@jmonkeystore.com", "JmonkeyStore"));
        message.setTo(user.getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = templateEngine.process("/mail/html/registration.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        log.info("Sending registration email to: " + user.getEmail());
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sendAccountValidationEmail(User user, UserValidation validation) throws UnsupportedEncodingException, MessagingException {

        if (!ServerConfig.getInstance().getWebsiteConfig().isEmailEnabled()) {
            return;
        }

        final Context ctx = new Context(Locale.UK);

        // Prepare the evaluation context
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("validationCode", validation.getId());
        ctx.setVariable("changeType", validation.getValidationType().name());

        if (validation.getValidationType() == AccountValidationType.Email) {
            ctx.setVariable("newEmail", validation.getValue());
        }


        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject("JmonkeyStore");
        message.setFrom(new InternetAddress("noreply@jmonkeystore.com", "JmonkeyStore"));
        message.setTo(user.getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = templateEngine.process("/mail/html/account-validation.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        log.info(String.format("Sending validation email of type '%s' to %s", validation.getValidationType(), user.getEmail()));
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sentUnderReviewEmail(StorePage storePage) throws MessagingException, UnsupportedEncodingException {

        if (!ServerConfig.getInstance().getWebsiteConfig().isEmailEnabled()) {
            return;
        }

        final Context ctx = new Context(Locale.UK);

        boolean isAmendment = storePage instanceof PageAmendment;

        // Prepare the evaluation context
        ctx.setVariable("username", storePage.getOwner().getUsername());
        ctx.setVariable("page", storePage);
        ctx.setVariable("amendment", isAmendment);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject("JmonkeyStore");
        message.setFrom(new InternetAddress("noreply@jmonkeystore.com", "JmonkeyStore"));
        message.setTo(storePage.getOwner().getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = templateEngine.process("/mail/html/page-under-review.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        log.info(String.format("Sending under-review email for '%s' to %s", storePage.getDetails().getTitle(), storePage.getOwner().getEmail()));
        javaMailSender.send(mimeMessage);

    }

    @Async
    public void sendApprovalEmail(StorePage storePage, boolean wasAmendment) throws MessagingException, UnsupportedEncodingException {

        if (!ServerConfig.getInstance().getWebsiteConfig().isEmailEnabled()) {
            return;
        }

        final Context ctx = new Context(Locale.UK);

        // Prepare the evaluation context
        ctx.setVariable("username", storePage.getOwner().getUsername());
        ctx.setVariable("page", storePage);
        ctx.setVariable("amendment", wasAmendment);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject("JmonkeyStore");
        message.setFrom(new InternetAddress("noreply@jmonkeystore.com", "JmonkeyStore"));
        message.setTo(storePage.getOwner().getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = templateEngine.process("/mail/html/page-approved.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        log.info(String.format("Sending approval email for '%s' to %s", storePage.getDetails().getTitle(), storePage.getOwner().getEmail()));
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sendRejectionEmail(StorePage page, String reason) throws MessagingException, UnsupportedEncodingException {

        if (!ServerConfig.getInstance().getWebsiteConfig().isEmailEnabled()) {
            return;
        }

        final Context ctx = new Context(Locale.UK);

        String htmlReason = reason.replace(System.lineSeparator(), "<br />");

        // Prepare the evaluation context
        ctx.setVariable("username", page.getOwner().getUsername());
        ctx.setVariable("page", page);
        ctx.setVariable("reason", htmlReason);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject("JmonkeyStore");
        message.setFrom(new InternetAddress("noreply@jmonkeystore.com", "JmonkeyStore"));
        message.setTo(page.getOwner().getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = templateEngine.process("/mail/html/page-rejected.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        log.info(String.format("Sending rejection email for '%s' to %s", page.getDetails().getTitle(), page.getOwner().getEmail()));
        javaMailSender.send(mimeMessage);

    }

    @Async
    public void sendResetPasswordEmail(User user, String validationCode, String ipAddressFrom) throws MessagingException, UnsupportedEncodingException {

        if (!ServerConfig.getInstance().getWebsiteConfig().isEmailEnabled()) {
            return;
        }

        final Context ctx = new Context(Locale.UK);

        // Prepare the evaluation context
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("validationCode", validationCode);
        ctx.setVariable("ipAddressFrom", ipAddressFrom);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject("JmonkeyStore");
        message.setFrom(new InternetAddress("noreply@jmonkeystore.com", "JmonkeyStore"));
        message.setTo(user.getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = templateEngine.process("/mail/html/reset-lost-password.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        log.info("Sending registration email to: " + user.getEmail());
        javaMailSender.send(mimeMessage);

    }

    @Async
    public void notifyStaffReviewRequested(StorePage storePage) {

        List<User> staff = userRepository.findByAdministratorTrueOrModeratorTrue();

        log.info(String.format("Sending approval notification to %d staff members.", staff.size()));

        boolean amendment = storePage instanceof PageAmendment;

        for (User user : staff) {

            try {
                final Context ctx = new Context(Locale.UK);
                ctx.setVariable("username", user.getUsername());
                ctx.setVariable("page", storePage);
                ctx.setVariable("pagetype", amendment? "amendment" : "draft");

                final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                final MimeMessageHelper message =
                        new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart

                message.setSubject("JmonkeyStore - Review Requested");
                message.setFrom(new InternetAddress("noreply@jmonkeystore.com", "JmonkeyStore"));
                message.setTo(user.getEmail());

                // Create the HTML body using Thymeleaf
                final String htmlContent = templateEngine.process("/mail/html/review-requested.html", ctx);
                message.setText(htmlContent, true); // true = isHtml

                // Send mail
                javaMailSender.send(mimeMessage);
            } catch (MessagingException | UnsupportedEncodingException e) {
                // e.printStackTrace();
                log.info(String.format("Unable to send approval email to [ %s | %s ] Reason: %s", user.getUsername(), user.getEmail(), e.getMessage()));
            }
        }

        log.info(String.format("Sent approval notification to %d staff members.", staff.size()));

    }
}
