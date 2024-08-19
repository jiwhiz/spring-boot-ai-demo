package com.jiwhiz.demo.external;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;

import com.jiwhiz.demo.common.ApplicationProperties;
import com.jiwhiz.demo.user.User;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
@RequiredArgsConstructor
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final ApplicationProperties appProperties;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
    }

    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
    }

    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, appProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {

        if (!appProperties.getMail().isEnabled()) {
            log.debug(
                "Send email {}{} to '{}' with subject '{}' and content=\n{}",
                isMultipart ? "with multipart" : "",
                isHtml ? " and html" : "",
                to,
                subject,
                content
            );
            return;
        }

        Email emailFrom = new Email(appProperties.getMail().getFrom());
        Email emailTo = new Email(to);
        Content emailContent = new Content(isHtml? "text/html": "text/plain", content);
        Mail mail = new Mail(emailFrom, subject, emailTo, emailContent);

        SendGrid sg = new SendGrid(appProperties.getMail().getApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info("SendGrid response status code: {}", response.getStatusCode());
            log.info("Response Header: {}", response.getHeaders());
        } catch (Exception ex) {
            log.warn("Cannot send email through SendGrid.", ex);
        }
    }

}

