package org.phototimemachine.service.validator.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service("customEmailSender")
public class CustomEmailSender {

    private MailSender mailSender;
    private final String from = "hello@phototimemachine.org";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegister(String to, Locale locale) {
        String subject = messageSource.getMessage("message.sending.register.theme", new Object[]{}, locale);
        String message = messageSource.getMessage("message.sending.register.message", new Object[]{}, locale);

        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(from);
        smm.setTo(to);
        smm.setSubject(subject);
        smm.setText(message);
        mailSender.send(smm);
    }

    public void restorePassword(String to, String password, Locale locale) {
        String subject = messageSource.getMessage("message.sending.register.theme", new Object[]{}, locale);
        String message = messageSource.getMessage("message.sending.restore.message", new Object[]{}, locale);

        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(from);
        smm.setTo(to);
        smm.setSubject(subject);
        smm.setText(message + " " + password);
        mailSender.send(smm);
    }

    public void simpleSend(String to, String message) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(from);
        smm.setTo(to);
        smm.setSubject("Historical Letter");
        smm.setText(message);
        mailSender.send(smm);
    }
}
