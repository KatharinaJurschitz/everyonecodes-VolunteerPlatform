package org.scrumbledores.user.admin;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.dataclass.OrgIndDTO;
import org.scrumbledores.user.dataclass.PlatformDTO;
import org.scrumbledores.user.dataclass.PlatformUser;
import org.scrumbledores.user.dataclass.VolunteerDTO;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminEndpoint {

    private final AdminService service;

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    PlatformUser create(@RequestBody PlatformUser adminUser) {
        return service.create(adminUser);
    }

    @GetMapping("/volunteers")
    @Secured({"ROLE_ADMIN"})
    List<VolunteerDTO> getAllVolunteers() {
        return service.getAllVolunteers();
    }

    @GetMapping("/organizations")
    @Secured({"ROLE_ADMIN"})
    List<OrgIndDTO> getAllOrganizations() {
        return service.getAllOrganizations();
    }

    @GetMapping("/individuals")
    @Secured({"ROLE_ADMIN"})
    List<OrgIndDTO> getAllIndividuals() {
        return service.getAllIndividuals();
    }

    @GetMapping("/details/{username}")
    @Secured({"ROLE_ADMIN"})
    PlatformUser getAllDetails(@PathVariable String username) {
        return service.getAllDetails(username).orElse(null);
    }

}
