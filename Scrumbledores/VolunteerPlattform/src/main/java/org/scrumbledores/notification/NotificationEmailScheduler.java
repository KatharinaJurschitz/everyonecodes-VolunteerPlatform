package org.scrumbledores.notification;

import lombok.AllArgsConstructor;
import org.scrumbledores.email.EmailService;
import org.scrumbledores.user.PlatformUserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@EnableScheduling
@EnableAsync
public class NotificationEmailScheduler {

    private final EmailService emailService;
    private final PlatformUserRepository repository;

    @Async
    @Scheduled(cron = "${scheduling.senddaily}")
    public void sendDaily() {
        send("daily");
    }

    @Async
    @Scheduled(cron = "${scheduling.sendweekly}")
    public void sendWeekly() {
        send("weekly");
    }

    @Async
    @Scheduled(cron = "${scheduling.sendmonthly}")
    public void sendMonthly() {
        send("monthly");
    }

    private void send(String frequency) {
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
}
