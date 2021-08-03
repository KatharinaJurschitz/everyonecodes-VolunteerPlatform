package org.scrumbledores.user.authentificationHandler;

import lombok.AllArgsConstructor;
import org.scrumbledores.email.EmailService;
import org.scrumbledores.user.PlatformUserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final PlatformUserRepository repository;
    private final EmailService emailService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        var username = event.getAuthentication().getPrincipal();
        var oUser = repository.findOneByUsername(username.toString());
        if (oUser.isPresent()) {
            var user = oUser.get();
            user.setFailedLoginAttempt(user.getFailedLoginAttempt()+1);
            repository.save(user);
            if (user.getFailedLoginAttempt() > 4) {
                String emailTo = user.getEmail();
                String subject = "Failed Login Attempts at Volunteer Platform";
                String email = "Someone tried to login to your account 5x with wrong password.";
                emailService.sendEmail(emailTo, subject, email);
                user.setFailedLoginAttempt(0);
                repository.save(user);
            }
        }
    }
}
