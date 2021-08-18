package org.scrumbledores.activity;

import lombok.AllArgsConstructor;
import org.scrumbledores.notification.NotificationService;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.scrumbledores.user.dataclass.Activity;
import org.scrumbledores.user.dataclass.ActivityDTO;
import org.scrumbledores.user.dataclass.ActivityVolunteerDTO;
import org.scrumbledores.user.dataclass.Rating;
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
    private final NotificationService notificationService;

    public ActivityDTO createActivity(ActivityDTO activityDTO, Principal principal) {
        var user = userService.findUser(principal);

        var activity = activityDTOToActivity(activityDTO);

        activity.setStatus("draft");
        activity.setCreatorName(principal.getName());
        activity.setCreatorRole(user.getRole().stream().findFirst().get());
        activity.setCreatorRating(user.getRating());

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

    public String sendInviteToVolunteer(Principal principal, String id, String username) {
        var oUser = repository.findOneByUsername(principal.getName());
        if (oUser.isEmpty()) {
            return "hearst deppata";
        }
        var user = oUser.get();

        var oVolunteer = repository.findOneByUsername(username);
        if (oVolunteer.isEmpty()) {
            return "Volunteer not found";
        }
        var volunteer = oVolunteer.get();

        var oActivity = user.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("in progress"))
                .findFirst();

        if (oActivity.isEmpty()) {
            return "Activity not found or not 'in progress'.";
        }
        var activity = oActivity.get();
        activity.setStatus("pending");
        volunteer.getActivities().add(activity);
        repository.save(volunteer);
        String message = "Hello " + volunteer.getUsername() + ", you are invited to help with this Activity: " + id + ". Please accept or deny. Thank you.";

        if (volunteer.getNotifications().stream()
                .anyMatch(e -> e.contains(message))) {
            return "Invitation can't be sent again.";
        }
        notificationService.sendNotification(user.getUsername(), volunteer.getUsername(), message);

        return "Notification was sent.";
    }

    public String acceptDenyInvitation(Principal principal, String id, String acceptdeny) {
        var oVolunteer = repository.findOneByUsername(principal.getName());
        if (oVolunteer.isEmpty()) {
            return "hearst deppata";
        }
        var volunteer = oVolunteer.get();

        var oCreator = repository.findOneByActivitiesActivityId(id);
        if (oCreator.isEmpty()) {
            return "Activity not found.";
        }
        var creator = oCreator.stream()
                .filter(x -> !x.getRole().contains("ROLE_VOLUNTEER"))
                .findFirst()
                .get();

        var oActivity = volunteer.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("pending"))
                .findFirst();

        if (oActivity.isEmpty()) {
            return "Activity not found or not 'pending'.";
        }
        var activity = oActivity.get();

        if (acceptdeny.equals("accept")) {

            volunteer.getActivities().remove(activity);
            activity.setStatus("in progress");
            volunteer.getActivities().add(activity);
            repository.save(volunteer);

            var oActivityCreator = creator.getActivities().stream()
                    .filter(x -> x.getActivityId().equals(id))
                    .findFirst();
            if (oActivityCreator.isEmpty()) {
                return "Activity not found.";
            }
            var activityCreator = oActivityCreator.get();

            volunteer.getActivities().remove(activity);
            activity.getRatings().add(new Rating(creator));
            volunteer.getActivities().add(activity);

            creator.getActivities().remove(activityCreator);
            activityCreator.getRatings().add(new Rating(volunteer));
            creator.getActivities().add(activityCreator);

            repository.save(creator);
            repository.save(volunteer);

            notificationService.sendNotification(volunteer.getUsername(), creator.getUsername(), volunteer.getUsername() + " has accepted your invitation for id: " + id);

            return "You accepted activity id: " + id;

        } else if (acceptdeny.equals("deny")) {
            volunteer.getActivities().remove(activity);
            repository.save(volunteer);
            notificationService.sendNotification(volunteer.getUsername(), creator.getUsername(), volunteer.getUsername() + " has declined your invitation for id: " + id);
            return "You denied activity id: " + id;

        } else {
            return "Please type either 'accept' or 'deny'.";

        }
    }

    public String sendApplicationToOrgInd(Principal principal, String id) {
        var volunteer = userService.findUser(principal);
        var oCreator = repository.findOneByActivitiesActivityId(id);
        if (oCreator.isEmpty()) {
            return "Activity not found.";
        }
        var creator = oCreator.stream()
                .filter(x -> !x.getRole().contains("ROLE_VOLUNTEER"))
                .findFirst()
                .get();

        var oActivity = creator.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("in progress"))
                .findFirst();
        if (oActivity.isEmpty()) {
            return "Activity not found or not 'in progress'.";
        }
        var activity = oActivity.get();
        activity.setStatus("pending");
        volunteer.getActivities().add(activity);
        repository.save(volunteer);
        String message = volunteer.getUsername() + " has applied for activity id: " + id;
        notificationService.sendNotification(volunteer.getUsername(), creator.getUsername(), message);

        return "Application for activity " + id + " was sent.";
    }

    public String acceptDenyApplication(Principal principal, String id, String username, String acceptdeny) {
        var oOrgInd = repository.findOneByUsername(principal.getName());
        if (oOrgInd.isEmpty()) {
            return "hearst deppata";
        }
        var orgInd = oOrgInd.get();

        var volunteers = repository.findOneByActivitiesActivityId(id);
        if (volunteers.isEmpty()) {
            return "No volunteers found for this activity.";
        }
        var oVolunteer = volunteers.stream()
                .filter(x -> x.getUsername().equals(username))
                .findFirst();
        if (oVolunteer.isEmpty()) {
            return "Volunteer not found.";
        }
        var volunteer = oVolunteer.get();

        var oActivity = orgInd.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("in progress"))
                .findFirst();

        if (oActivity.isEmpty()) {
            return "Activity not found or not 'in progress'.";
        }
        var activity = oActivity.get();

        var oActivityVolunteer = volunteer.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .findFirst();
        if (oActivityVolunteer.isEmpty()) {
            return "Activity not found.";
        }
        var activityVolunteer = oActivityVolunteer.get();

        if (acceptdeny.equals("accept")) {
            orgInd.getActivities().remove(activity);
            activity.getRatings().add(new Rating(volunteer));
            orgInd.getActivities().add(activity);

            volunteer.getActivities().remove(activityVolunteer);
            activityVolunteer.getRatings().add(new Rating(orgInd));
            activityVolunteer.setStatus("in progress");
            volunteer.getActivities().add(activityVolunteer);

            repository.save(volunteer);
            repository.save(orgInd);

            notificationService.sendNotification(orgInd.getUsername(), volunteer.getUsername(), volunteer.getUsername() + ", your application for id: " + id + " was accepted.");

            return "You accepted the application by " + volunteer.getUsername() + " for activity id: " + id;

        } else if (acceptdeny.equals("deny")) {
            volunteer.getActivities().remove(activityVolunteer);
            repository.save(volunteer);

            notificationService.sendNotification(orgInd.getUsername(), volunteer.getUsername(), volunteer.getUsername() + ", your application for id: " + id + " was declined.");

            return "You denied the application by " + volunteer.getUsername() + " for activity id: " + id;

        } else {
            return "Please type either 'accept' or 'deny'.";

        }
    }

    public List<ActivityVolunteerDTO> getOwnActivitiesAsVolunteer(Principal principal) {
        var volunteer = userService.findUser(principal);
        return volunteer.getActivities().stream()
                .map(this::activityToActivityVolunteerDTO)
                .collect(Collectors.toList());
    }

    private ActivityVolunteerDTO activityToActivityVolunteerDTO(Activity activity) {
        List<Rating> ratings = activity.getRatings();

        return new ActivityVolunteerDTO(
                activity.getTitle(),
                activity.getDescription(),
                activity.getStatus(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.getCreatorName(),
                activity.getCreatorRole(),
                activity.getCreatorRating(),
                ratings.stream().map(Rating::getRatingFromParticipant).findFirst().orElse(0),
                ratings.stream().filter(x -> x.getFeedbackFromParticipant() != null).map(Rating::getFeedbackFromParticipant).findFirst().orElse(""),
                ratings.stream().map(Rating::getRatingFromCreator).findFirst().orElse(0),
                ratings.stream().filter(x -> x.getFeedbackFromCreator() != null).map(Rating::getFeedbackFromCreator).findFirst().orElse("")
        );
    }
}
