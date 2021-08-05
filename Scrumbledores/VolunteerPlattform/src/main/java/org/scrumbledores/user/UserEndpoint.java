package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserEndpoint {

    private final UserService service;

    @PostMapping
    PlatformUser create(@Valid @RequestBody PlatformUser user) {
        var oUser = service.createUser(user);
        if (oUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Creation Error");
        }
        return  oUser.get();
    }

    @GetMapping("/login")
    @Secured({"ROLE_VOLUNTEER", "ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    PlatformUser login(Principal principal) {
        return service.findUser(principal);
    }

    @GetMapping("/profile")
    @Secured({"ROLE_VOLUNTEER", "ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    PlatformDTO showPersonalData(Principal principal) {
        return service.showPersonalData(principal);
    }

    @PutMapping("/profile/edit")
    @Secured({"ROLE_VOLUNTEER", "ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    PlatformDTO editPersonalData(@RequestBody PlatformDTO dto, Principal principal) {
        return service.editPersonalData(dto, principal);
    }
    @GetMapping("/profile/public")
    @Secured({"ROLE_VOLUNTEER", "ROLE_ORGANIZATION", "ROLE_INDIVIDUAL"})
    UserPublicDTO showOwnPublicData(Principal principal) {
        return service.showOwnPublicData(principal);
    }

}
