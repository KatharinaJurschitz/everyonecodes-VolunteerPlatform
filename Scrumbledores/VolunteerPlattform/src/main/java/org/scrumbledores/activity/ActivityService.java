package org.scrumbledores.activity;

import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.scrumbledores.notification.NotificationService;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.scrumbledores.user.dataclass.*;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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

//        activity.setStatus("draft"); // PRODUCTION STATUS
        activity.setStatus("in progress"); // TEST STATUS
        activity.setTimestamp(LocalDate.now()); // TEST TIME STAMP
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
        activity.setTimestamp(LocalDate.now());
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
        activity.getRatings().clear();
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
            activity.getRatings().add(new Rating(volunteer));
            volunteer.getActivities().add(activity);
            repository.save(volunteer);

            var oActivityCreator = creator.getActivities().stream()
                    .filter(x -> x.getActivityId().equals(id))
                    .findFirst();
            if (oActivityCreator.isEmpty()) {
                return "Activity not found.";
            }
            var activityCreator = oActivityCreator.get();

//            volunteer.getActivities().remove(activity);
//            activity.getRatings().add(new Rating(creator));
//            volunteer.getActivities().add(activity);

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
        activity.getRatings().clear();
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
//            activityVolunteer.getRatings().add(new Rating(orgInd));
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


    public String completeActivityCreator(Principal principal, String id, List<Feedback> feedbacks) {
        var creator = userService.findUser(principal);
        var oCreatorActivity = creator.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("in progress"))
                .findFirst();
        if (oCreatorActivity.isEmpty()) {
            return "Activity not found or not 'in progress'.";
        }
        var creatorActivity = oCreatorActivity.get();


        var ratings = creatorActivity.getRatings();

        if (ratings.size() != feedbacks.size()) {
            return "Please rate all participants";
        }

        var participantsRating = ratings.stream()
                .sorted(Comparator.comparing(x -> x.getParticipant().getUsername()))
                .collect(Collectors.toList());
        var sortedFeedback = feedbacks.stream()
                .sorted(Comparator.comparing(Feedback::getUsername))
                .collect(Collectors.toList());
        for (int i = 0; i < participantsRating.size(); i++) {
            if (!participantsRating.get(i).getParticipant().getUsername().equals(sortedFeedback.get(i).getUsername())) {
                return "Not the same username(s)";
            }
        }
        for (int i = 0; i < participantsRating.size(); i++) {
            participantsRating.get(i).setRatingFromCreator(sortedFeedback.get(i).getRating());
            var volunteer = repository.findOneByUsername(sortedFeedback.get(i).getUsername()).get();
            var volunteerActivity = volunteer.getActivities().stream()
                    .filter(x -> x.getActivityId().equals(id))
                    .findFirst()
                    .get();

            volunteer.getActivities().remove(volunteerActivity);

            volunteerActivity.getRatings().get(0).setRatingFromCreator(sortedFeedback.get(i).getRating());

            if (!sortedFeedback.get(i).getFeedback().isEmpty()) {
                participantsRating.get(i).setFeedbackFromCreator(sortedFeedback.get(i).getFeedback());
                volunteerActivity.getRatings().get(0).setFeedbackFromCreator(sortedFeedback.get(i).getFeedback());
            }

            volunteer.getActivities().add(volunteerActivity);

            if (volunteer.getRating() == 0) {
                volunteer.setRating(sortedFeedback.get(i).getRating());
                volunteer.getRatings().add(sortedFeedback.get(i).getRating());
            } else {
                volunteer.getRatings().add(sortedFeedback.get(i).getRating());
                var integer = volunteer.getRatings().stream().reduce(0, Integer::sum);
                volunteer.setRating((double) integer / (double) volunteer.getRatings().size());
            }
            volunteerActivity.setStatus("To be rated");
            repository.save(volunteer);
            String message = "Activity Id " + id + " was closed by " + creator.getUsername() + ". You have been rated." +
                    "Please rate " + creator.getUsername() + " to close this activity completely.";
            notificationService.sendNotification(creator.getUsername(), volunteer.getUsername(), message);
        }
        creatorActivity.setStatus("completed");
        creator.setExp(creator.getExp()+1);
        repository.save(creator);
        return "Activity Id: " + id + " was completed.";
    }

    public String completeActivityVolunteer(Principal principal, String id, Feedback feedback) {
        var volunteer = userService.findUser(principal);
        var oVolunteerActivity = volunteer.getActivities()
                .stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("To be rated"))
                .findFirst();
        if (oVolunteerActivity.isEmpty()) {
            return "Activity not found or not 'to be rated'.";
        }
        var volunteerActivity = oVolunteerActivity.get();
        var creator = repository.findOneByUsername(volunteerActivity.getCreatorName()).get();
        var oCreatorActivity = creator.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("completed"))
                .findFirst();
        if (oCreatorActivity.isEmpty()) {
            return "Creator Activity not found or not 'completed'.";
        }
        var creatorActivity = oCreatorActivity.get();
        var oVolunteerRatingFromCreator = creatorActivity.getRatings()
                .stream()
                .filter(x -> x.getParticipant().getUsername().equals(volunteer.getUsername()))
                .findFirst();
        if (oVolunteerRatingFromCreator.isEmpty()) {
            return "Rating with Volunteer Name not found.";
        }
        var volunteerRatingFromCreator = oVolunteerRatingFromCreator.get();
        var volunteerRatingFromVolunteer = volunteerActivity.getRatings().get(0);

        volunteer.getActivities().remove(volunteerActivity);
        creator.getActivities().remove(creatorActivity);

        volunteerRatingFromCreator.setRatingFromParticipant(feedback.getRating());
        volunteerRatingFromVolunteer.setRatingFromParticipant(feedback.getRating());

        if (!feedback.getFeedback().isEmpty()) {
            volunteerRatingFromCreator.setFeedbackFromCreator(feedback.getFeedback());
            volunteerRatingFromVolunteer.setFeedbackFromParticipant(feedback.getFeedback());
        }

        if (creator.getRating() == 0) {
            creator.setRating(feedback.getRating());
            creator.getRatings().add(feedback.getRating());
        } else {
            creator.getRatings().add(feedback.getRating());
            var integer = creator.getRatings().stream().reduce(0, Integer::sum);
            creator.setRating((double) integer / (double) creator.getRatings().size());
        }
        volunteer.setExp(volunteer.getExp()+1);
        volunteerActivity.setStatus("completed");
        volunteer.getActivities().add(volunteerActivity);
        creator.getActivities().add(creatorActivity);

        repository.save(volunteer);
        repository.save(creator);

        String message = "Activity Id " + id + " was closed by " + volunteer.getUsername() + ". You have been rated";
        notificationService.sendNotification(volunteer.getUsername(), creator.getUsername(), message);

        return "Activity ID " + id + " was completed.";
    }

    public String deleteActivity(Principal principal, String id) {
        var creator = userService.findUser(principal);

        var oCreatorActivity = creator.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getRatings().isEmpty())
                .findFirst();
        if (oCreatorActivity.isEmpty()) {
            return "Creator Activity not found or already connected to Participants";
        }
        var creatorActivity = oCreatorActivity.get();
        creator.getActivities().remove(creatorActivity);
        repository.save(creator);

        return "Activity " + id + " was successfully deleted";
    }

    public String changeToDraft(Principal principal, String id) {
        var creator = userService.findUser(principal);

        var oCreatorActivity = creator.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getRatings().isEmpty())
                .findFirst();
        if (oCreatorActivity.isEmpty()) {
            return "Creator Activity not found or already connected to Participants";
        }
        var creatorActivity = oCreatorActivity.get();
        creator.getActivities().remove(creatorActivity);
        creatorActivity.setStatus("draft");
        creator.getActivities().add(creatorActivity);
        repository.save(creator);

        return "Activity " + id + " was successfully changed to Draft Status";
    }

    public String withdrawApplication(Principal principal, String id) {
        var volunteer = userService.findUser(principal);

        var oVolunteerActivity = volunteer.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("pending") || x.getStatus().equals("in progress"))
                .findFirst();
        if (oVolunteerActivity.isEmpty()) {
            return "Activity not found or already completed.";
        }
        var volunteerActivity = oVolunteerActivity.get();

        volunteer.getActivities().remove(volunteerActivity);
        repository.save(volunteer);

        var creator = repository.findOneByUsername(volunteerActivity.getCreatorName()).get();

        var oCreatorActivity = creator.getActivities().stream()
                .filter(x -> x.getActivityId().equals(id))
                .filter(x -> x.getStatus().equals("in progress"))
                .findFirst();
        if (oCreatorActivity.isEmpty()) {
            return "Creator Activity not found or not 'in progress'.";
        }
        var creatorActivity = oCreatorActivity.get();

        var oVolunteerRatingFromCreator = creatorActivity.getRatings()
                .stream()
                .filter(x -> x.getParticipant().getUsername().equals(volunteer.getUsername()))
                .findFirst();
        if (oVolunteerRatingFromCreator.isEmpty()) {
            String message = volunteer.getUsername() + " withdrew their application to activity id " + id;
            notificationService.sendNotification(volunteer.getUsername(), creator.getUsername(), message);
            return "Your application for activity id " + id + " was withdrawn.";
        }
        var volunteerRatingInCreatorActivity = oVolunteerRatingFromCreator.get();

        creatorActivity.getRatings().remove(volunteerRatingInCreatorActivity);
        repository.save(creator);

        String message = volunteer.getUsername() + " withdrew their application to activity id " + id;
        notificationService.sendNotification(volunteer.getUsername(), creator.getUsername(), message);
        return "Your application for activity id " + id + " was withdrawn.";
    }

}
