package org.scrumbledores.activity;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.dataclass.ActivityDTO;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/activity")
@AllArgsConstructor
public class ActivityEndpoint {

    private final ActivityService service;

    @PostMapping()
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    ActivityDTO createActivity(@Valid @RequestBody ActivityDTO activity, Principal principal) {
        return service.createActivity(activity, principal);
    }

    @GetMapping
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    LocalDateTime time() {
        return LocalDateTime.now();
    }


}
