package org.scrumbledores.notification;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final PlatformUserRepository repository;
    private final UserService userService;

    public void sendNotification(String sender, String recipient, String message) {
        var oUser = repository.findOneByUsername(recipient);

        if (oUser.isPresent()) {
            var user = oUser.get();
            message = "From: " + sender + ", Message: " + message;
            user.getNotifications().add(0, message);
            repository.save(user);
        }
    }

    public List<String> listAllNotifications(Principal principal) {
        var user = userService.findUser(principal);
        return user.getNotifications();
    }
}
