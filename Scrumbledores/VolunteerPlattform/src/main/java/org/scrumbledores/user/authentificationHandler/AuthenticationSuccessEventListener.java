package org.scrumbledores.user.authentificationHandler;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.PlatformUserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final PlatformUserRepository repository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        var username = event.getAuthentication().getPrincipal();
        var oUser = repository.findOneByUsername(username.toString());
        if (oUser.isPresent()) {
            var user = oUser.get();
            user.setFailedLoginAttempt(0);
            repository.save(user);
            }
        }
    }
