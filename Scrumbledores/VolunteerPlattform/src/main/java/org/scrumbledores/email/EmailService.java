package org.scrumbledores.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String email) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,  true, "utf-8");

            helper.setTo(to);
            helper.setText(email, true);
            helper.setSubject(subject);
            helper.setFrom("scrumbledore.email@gmail.com");
            mailSender.send(mimeMessage);
            
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}
