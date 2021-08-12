package org.scrumbledores.apptree;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.scrumbledores.user.UserService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@ConfigurationProperties("apptree")
@AllArgsConstructor
@Setter
public class AppTreeService {

    private final UserService service;
    private List<String> actions;

    public String showAppTree(Principal principal) {

        var user = service.findUser(principal);
        var role = new ArrayList<>(user.getRole()).get(0);

        return switch (role) {
            case "ROLE_VOLUNTEER" -> actions.get(0);
            case "ROLE_ORGANIZATION" -> actions.get(1);
            default -> actions.get(2);
        };
    }

}
