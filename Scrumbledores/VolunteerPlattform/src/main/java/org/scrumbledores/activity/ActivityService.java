package org.scrumbledores.activity;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.scrumbledores.user.dataclass.Activity;
import org.scrumbledores.user.dataclass.ActivityDTO;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Activity> getOwnActivitiesAsIndOrg(Principal principal) {
        var user = userService.findUser(principal);

        return user.getActivities();
    }

    public List<Activity> getOwnDrafts(Principal principal) {
        var user = userService.findUser(principal);
        var activities = user.getActivities();

        return activities.stream()
                .filter(e -> e.getStatus().equals("draft"))
                .collect(Collectors.toList());
    }

    public Optional<Activity> editOwnDraft(Principal principal, String id, ActivityDTO dto) {
        var activities = getOwnActivitiesAsIndOrg(principal);

        var oActivity = activities.stream()
                .filter(x -> x.getStatus().equals("draft"))
                .filter(x -> x.getActivityId().equals(id))
                .findFirst();

        if (oActivity.isEmpty()) {
            return Optional.empty();
        }

        var activity = oActivity.get();

        activities.remove(activity);

        if (!dto.getTitle().isEmpty()) {
            activity.setTitle(dto.getTitle());
        }
        if (!dto.getDescription().isEmpty()) {
            activity.setDescription(dto.getDescription());
        }
        if (dto.getStartDate() != null && dto.getStartDate().isAfter(LocalDateTime.now())) {
            activity.setStartDate(dto.getStartDate());
        }
        activity.getCategories().clear();
        activity.getCategories().addAll(dto.getCategories());

        activity.setEndDate(dto.getEndDate());
        activity.setRecommendedSkills(dto.getRecommendedSkills());

        activities.add(activity);

        var user = userService.findUser(principal);
        user.setActivities(activities);
        repository.save(user);
        return Optional.of(activity);
    }


    public String postOwnDraft(Principal principal, String id) {
        var activities = getOwnActivitiesAsIndOrg(principal);

        var oActivity = activities.stream()
                .filter(x -> x.getStatus().equals("draft"))
                .filter(x -> x.getActivityId().equals(id))
                .findFirst();

        if (oActivity.isEmpty()) {
            return "Draft not found";
        }
        var activity = oActivity.get();

        activities.remove(activity);
        activity.setStatus("in progress");
        activities.add(activity);

        var user = userService.findUser(principal);
        user.setActivities(activities);
        repository.save(user);

        return "Activity " + id + " was posted";
    }
}
