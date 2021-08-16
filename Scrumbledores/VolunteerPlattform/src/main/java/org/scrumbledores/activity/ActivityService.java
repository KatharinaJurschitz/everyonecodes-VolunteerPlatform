package org.scrumbledores.activity;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.scrumbledores.user.dataclass.Activity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class ActivityService {

    private final PlatformUserRepository repository;
    private final UserService userService;

    public Activity createActivity(Activity activity,  Principal principal) {
        var user = userService.findUser(principal);

        return activity;

    }

}
