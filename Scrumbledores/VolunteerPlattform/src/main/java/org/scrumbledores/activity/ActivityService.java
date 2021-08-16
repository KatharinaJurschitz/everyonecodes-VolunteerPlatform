package org.scrumbledores.activity;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.scrumbledores.user.dataclass.Activity;
import org.scrumbledores.user.dataclass.ActivityDTO;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class ActivityService {

    private final PlatformUserRepository repository;
    private final UserService userService;

    public ActivityDTO createActivity(ActivityDTO activityDTO, Principal principal) {
        var user = userService.findUser(principal);

        var activity = activityDTOToActivity(activityDTO);

        activity.setStatus("draft");

        user.getActivities().add(activity);
        repository.save(user);
        return activityDTO;

    }

    public Activity activityDTOToActivity(ActivityDTO input) {

        var activity = new Activity(
                input.getTitle(),
                input.getDescription(),
                input.getStartDate()
        );
        if (input.getDescription() != null) {
            activity.setDescription(input.getDescription());
        }
        if (input.getRecommendedSkills() != null) {
            activity.setRecommendedSkills(input.getRecommendedSkills());
        }
        if (input.getCategories() != null) {
            activity.setCategories(input.getCategories());
        }
        if (input.getEndDate() != null) {
            activity.setEndDate(input.getEndDate());
        }
        return activity;

    }

}
