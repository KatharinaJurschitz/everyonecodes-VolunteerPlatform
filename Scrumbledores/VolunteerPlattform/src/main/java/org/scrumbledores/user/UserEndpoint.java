package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserEndpoint {

    private final UserService service;

    @PostMapping
    PlatformUser create(@Valid @RequestBody PlatformUser user) {
        var oUser = service.createUser(user);
        if (oUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role Definition Error");
        }
        return  oUser.get();
    }
}
