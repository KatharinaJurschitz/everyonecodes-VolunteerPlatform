package org.scrumbledores.apptree;

import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/apptree")
@AllArgsConstructor
public class AppTreeEndpoint {

    private final AppTreeService service;

    @GetMapping
    @Secured({"ROLE_VOLUNTEER", "ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    String showAppTree(Principal principal) {
        return service.showAppTree(principal);
    }

}
