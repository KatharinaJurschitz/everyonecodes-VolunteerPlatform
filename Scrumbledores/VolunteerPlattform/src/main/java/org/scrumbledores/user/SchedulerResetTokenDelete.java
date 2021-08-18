package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
@EnableAsync
@AllArgsConstructor
public class SchedulerResetTokenDelete {

    private final PasswordResetRepository repository;

    @Async
    @Scheduled(cron = "${scheduling.deleteresettoken}")
    public void deleteUnusedResetToken() {
        var list = repository.findAll();
        System.out.println(list);

        list.stream()
                .filter(e -> e.getValidTill().isBefore(LocalDateTime.now()))
                .forEach(repository::delete);
    }
}
