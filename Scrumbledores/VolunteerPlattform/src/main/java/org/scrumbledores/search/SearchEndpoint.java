package org.scrumbledores.search;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.dataclass.Activity;
import org.scrumbledores.user.dataclass.SearchUserDTO;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@AllArgsConstructor
public class SearchEndpoint {
    private final SearchService service;

    @GetMapping("/activities")
    @Secured({"ROLE_VOLUNTEER"})
    List<Activity> findAllActivities() {
        return service.findAllActivities();
    }

    @GetMapping("/volunteers")
    @Secured({"ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    List<SearchUserDTO> findAllVolunteers() {
        return service.findAllVolunteers();
    }
}

