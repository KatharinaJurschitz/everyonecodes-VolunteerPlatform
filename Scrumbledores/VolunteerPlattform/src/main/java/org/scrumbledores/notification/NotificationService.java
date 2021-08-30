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

            if (!user.getNotificationFrequency().isEmpty()) {
                user.getNotificationsToSend().add(message);
            }

            repository.save(user);
        }
    }

    public List<String> listAllNotifications(Principal principal) {
        var user = userService.findUser(principal);
        return user.getNotifications();
    }

    public String registerForEmailNotifications(Principal principal, String frequency) {
        if (!frequency.equals("daily") && !frequency.equals("monthly") && !frequency.equals("weekly")) {
            return "FREQUENCY NOT VALID";
        }

        var user = userService.findUser(principal);
        user.setNotificationFrequency(frequency);

        repository.save(user);

        return "You successfully registered for e-Mail Notifications on a " + frequency + " basis.";
    }

    public String unsubscribeEmail(String unsubscribe) {
        var oUser = repository.findByUnsubscribeId(unsubscribe);

        if (oUser.isEmpty()) {
            return "Hahaha :p";
        }

        var user = oUser.get();
        user.setUnsubscribeId("");
        user.setNotificationFrequency("");
        repository.save(user);
        return "Successfully Unsubscribed";
    }
}
