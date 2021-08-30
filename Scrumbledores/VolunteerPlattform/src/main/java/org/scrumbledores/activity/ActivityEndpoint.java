package org.scrumbledores.activity;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.dataclass.Activity;
import org.scrumbledores.user.dataclass.ActivityDTO;
import org.scrumbledores.user.dataclass.ActivityVolunteerDTO;
import org.scrumbledores.user.dataclass.Feedback;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activity")
@AllArgsConstructor
public class ActivityEndpoint {

    private final ActivityService service;

    @PostMapping()
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    ActivityDTO createActivity(@Valid @RequestBody ActivityDTO activity, Principal principal) {
        return service.createActivity(activity, principal);
    }

    @GetMapping
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    List<Activity> getOwnActivitiesAsIndOrg(Principal principal) {
        return service.getOwnActivitiesAsIndOrg(principal);
    }

    @GetMapping("/volunteer")
    @Secured({"ROLE_VOLUNTEER"})
    List<ActivityVolunteerDTO> getOwnActivitiesAsVolunteer(Principal principal) {
        return service.getOwnActivitiesAsVolunteer(principal);
    }

    @GetMapping("/drafts")
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    List<Activity> getOwnDrafts(Principal principal) {
        return service.getOwnDrafts(principal);
    }

    @PutMapping("/drafts/{id}")
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    Activity editOwnDraft(Principal principal, @PathVariable String id, @RequestBody ActivityDTO dto) {
        return service.editOwnDraft(principal, id, dto).orElse(null);
    }

    @PutMapping("/drafts/{id}/post")
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    String postOwnDraft(Principal principal, @PathVariable String id) {
        return service.postOwnDraft(principal, id);
    }

    @PutMapping("/{id}/invite/{username}")
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    String sendInviteToVolunteer(Principal principal, @PathVariable String id, @PathVariable String username) {
        return service.sendInviteToVolunteer(principal, id, username);
    }

    @PutMapping("/{id}/{acceptdeny}")
    @Secured({"ROLE_VOLUNTEER"})
    String acceptDenyInvitation(Principal principal, @PathVariable String id, @PathVariable String acceptdeny) {
        return service.acceptDenyInvitation(principal, id, acceptdeny);
    }

    @PutMapping("/{id}/apply")
    @Secured("ROLE_VOLUNTEER")
    String sendApplicationToOrgInd(Principal principal, @PathVariable String id) {
        return service.sendApplicationToOrgInd(principal, id);
    }

    @PutMapping("/{id}/{username}/{acceptdeny}")
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    String acceptDenyApplication(Principal principal, @PathVariable String id, @PathVariable String username, @PathVariable String acceptdeny) {
        return service.acceptDenyApplication(principal, id, username, acceptdeny);
    }

    @PutMapping("/{id}/complete")
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    String completeActivityCreator(Principal principal, @PathVariable String id, @Valid @RequestBody List<Feedback> feedbacks) {
        return service.completeActivityCreator(principal, id, feedbacks);
    }

    @PutMapping("/{id}/close")
    @Secured({"ROLE_VOLUNTEER"})
    String completeActivityVolunteer(Principal principal, @PathVariable String id, @Valid @RequestBody Feedback feedback) {
        return service.completeActivityVolunteer(principal, id, feedback);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    String deleteActivity(Principal principal, @PathVariable String id) {
        return service.deleteActivity(principal, id);
    }

    @PutMapping("/{id}/changeToDraft")
    @Secured({"ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    String changeToDraft(Principal principal, @PathVariable String id) {
        return service.changeToDraft(principal, id);
    }

    @PutMapping("/{id}/withdraw")
    @Secured("ROLE_VOLUNTEER")
    String withdrawApplication(Principal principal, @PathVariable String id) {
        return service.withdrawApplication(principal, id);
    }
}
