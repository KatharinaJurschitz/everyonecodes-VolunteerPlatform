package org.scrumbledores.notification;

import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationEndpoint {

    private final NotificationService service;

    @GetMapping
    @Secured({"ROLE_VOLUNTEER", "ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    List<String> listAllNotifications(Principal principal) {
        return service.listAllNotifications(principal);
    }

    @GetMapping("/keywords")
    @Secured("ROLE_VOLUNTEER")
    List<String> listAllKeywords(Principal principal) {
        return service.listAllKeywords(principal);
    }

    @PutMapping("/email/register/{frequency}")
    @Secured({"ROLE_VOLUNTEER", "ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    String registerForEmailNotifications(Principal principal, @PathVariable String frequency) {
        return service.registerForEmailNotifications(principal, frequency);
    }

    @PutMapping("/keyword/register/{keyword}")
    @Secured({"ROLE_VOLUNTEER"})
    String registerForKeywordNotifications(Principal principal, @PathVariable String keyword) {
        return service.registerForKeywordNotifications(principal, keyword);
    }

    @GetMapping("/email/unsubscribe")
    String unsubscribeEmail(@RequestParam String id) {
        return service.unsubscribeEmail(id);
    }

    @GetMapping("/keyword/unsubscribe")
    String unsubscribeKeyword(@RequestParam String username, @RequestParam String keyword) {
        return service.unsubscribeKeyword(username, keyword);
    }

}
