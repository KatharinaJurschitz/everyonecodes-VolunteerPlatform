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
            return "You already unsubscribed.";
        }

        var user = oUser.get();
        user.setUnsubscribeId("");
        user.setNotificationFrequency("");
        repository.save(user);
        return "Successfully unsubscribed";
    }

    public String unsubscribeKeyword(String username, String keyword) {
        var oUser = repository.findOneByUsername(username);

        if (oUser.isEmpty()) {
            return "User not found.";
        }

        var user = oUser.get();

        if (!user.getKeywords().contains(keyword)) {
            return "You already unsubscribed from this keyword.";
        }

        user.getKeywords().remove(keyword);
        repository.save(user);
        return "Successfully unsubscribed from keyword " + keyword;
    }

    public String registerForKeywordNotifications(Principal principal, String keyword) {
        var user = userService.findUser(principal);
        user.getKeywords().add(keyword);
        repository.save(user);
        return "You successfully registered for e-Mail Notifications for keyword " + keyword;
    }

    public List<String> listAllKeywords(Principal principal) {
        var user = userService.findUser(principal);
        return user.getKeywords();
    }
}
