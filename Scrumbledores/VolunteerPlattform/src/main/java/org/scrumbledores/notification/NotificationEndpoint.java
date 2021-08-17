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

    @PostMapping
    @Secured({"ROLE_VOLUNTEER", "ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    void testnotification() {
        service.sendNotification("Test", "Individual1", "Testmessage");
    }

 //(.)(.)
}
