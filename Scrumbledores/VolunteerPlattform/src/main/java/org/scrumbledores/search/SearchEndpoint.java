package org.scrumbledores.search;

import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.scrumbledores.user.dataclass.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    @GetMapping("/volunteers/{searchCriteria}")
    @Secured({"ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    List<UserPublicDTO> findAllVolunteersFiltered(@PathVariable String searchCriteria,
                                                  @RequestParam(defaultValue = "", required = false) String filterSkills,
                                                  @RequestParam(defaultValue = "0", required = false) String filterRating) {
        var result = service.findAllVolunteersFiltered(searchCriteria, filterSkills, filterRating);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nothing was found");
        }
        return result;
    }

    @GetMapping("/activities/{searchCriteria}")
    @Secured({"ROLE_VOLUNTEER"})
    List<ActivityDB> findAllActivitiesFiltered(@PathVariable String searchCriteria,
                                                  @RequestParam(defaultValue = "2000-01-01", required = false) String filterDate,
                                                  @RequestParam(defaultValue = "", required = false) String filterCategory,
                                                  @RequestParam(defaultValue = "", required = false) String filterSkills,
                                                  @RequestParam(defaultValue = "", required = false) String filterCreator,
                                                  @RequestParam(defaultValue = "0", required = false) String filterRating) {
        var result = service.findAllActivitiesFiltered(searchCriteria, filterDate, filterCategory, filterSkills, filterCreator, filterRating);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nothing was found");
        }
        return result;
    }
}