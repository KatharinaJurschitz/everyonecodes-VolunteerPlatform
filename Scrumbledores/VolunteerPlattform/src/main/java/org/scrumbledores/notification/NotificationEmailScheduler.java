package org.scrumbledores.notification;

import lombok.AllArgsConstructor;
import org.scrumbledores.email.EmailService;
import org.scrumbledores.search.SearchService;
import org.scrumbledores.user.PlatformUserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@EnableScheduling
@EnableAsync
public class NotificationEmailScheduler {

    private final EmailService emailService;
    private final PlatformUserRepository repository;
    private final SearchService searchService;

    @Async
    @Scheduled(cron = "${scheduling.senddaily}")
    public void sendDaily() {
        sendNotification("daily");
        sendKeyword("daily");
    }

    @Async
    @Scheduled(cron = "${scheduling.sendweekly}")
    public void sendWeekly() {
        sendNotification("weekly");
        sendKeyword("weekly");
    }

    @Async
    @Scheduled(cron = "${scheduling.sendmonthly}")
    public void sendMonthly() {
        sendNotification("monthly");
        sendKeyword("monthly");
    }

    private void sendNotification(String frequency) {
        repository.findAll().stream()
                .filter(x -> x.getNotificationFrequency().equals(frequency))
                .filter(x -> !x.getNotificationsToSend().isEmpty())
                .forEach(x -> {
                    String uuid = UUID.randomUUID().toString();
                    emailService.sendEmail(x.getEmail(),
                            "Your " + frequency + " notifications",
                            String.join(",<br>", x.getNotificationsToSend()) +
                                    "<br><br><a href=\"http://localhost:9000/notifications/email/unsubscribe?id=" + uuid + "\">Unsubscribe</a>");
                    x.getNotificationsToSend().clear();
                    x.setUnsubscribeId(uuid);
                    repository.save(x);
                });
    }

    private void sendKeyword(String frequency) {
        int daysToSubtract = 1;
        if (frequency.equals("weekly")) {
            daysToSubtract = 7;
        }
        if (frequency.equals("monthly")) {
            daysToSubtract = 31;
        }
        final int finalDaysToSubtract = daysToSubtract;

        repository.findAll().stream()
                .filter(user -> user.getNotificationFrequency().equals(frequency))
                .filter(user -> !user.getKeywords().isEmpty())
                .forEach(user -> {
                    user.getKeywords().forEach(keyword -> {
                        var activitiesToSend =  searchService.findAllActivitiesByKeyword(keyword).stream()
                                .filter(activity -> activity.getStatus().equals("in progress"))
                                .filter(activity -> activity.getTimestamp().isAfter(LocalDate.now().minusDays(finalDaysToSubtract)))
                                .map(Objects::toString)
                                .collect(Collectors.joining(" <br> <br>"));
                        if (!activitiesToSend.isEmpty()) {
                            emailService.sendEmail(user.getEmail(),
                                    "New activities for keyword: " + keyword,
                                    String.join(",<br><br>", activitiesToSend +
                                            "<br><br><br><a href=\"http://localhost:9000/notifications/keyword/unsubscribe?username=" + user.getUsername() + "&keyword=" + keyword + "\">Unsubscribe from this keyword</a>"));
                        }
                    });
                });
    }
}
