package org.scrumbledores.activity;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.dataclass.Activity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/activity")
@AllArgsConstructor
public class ActivityEndpoint {

    private final ActivityService service;

    @PostMapping()
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    Activity createActivity(@Valid @RequestBody Activity activity, Principal principal) {
        return service.createActivity(activity, principal);
    }


}
