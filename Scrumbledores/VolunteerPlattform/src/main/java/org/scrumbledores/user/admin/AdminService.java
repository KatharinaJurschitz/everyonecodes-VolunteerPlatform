package org.scrumbledores.user.admin;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.dataclass.OrgIndDTO;
import org.scrumbledores.user.dataclass.PlatformDTO;
import org.scrumbledores.user.dataclass.PlatformUser;
import org.scrumbledores.user.dataclass.VolunteerDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {

    private final PlatformUserRepository repository;

    public PlatformUser create(PlatformUser adminUser) {
        adminUser.setRole(Set.of("ROLE_ADMIN"));
        @Valid PlatformUser adminUser2 = adminUser;
        return repository.save(adminUser2);
    }

    public List<VolunteerDTO> getAllVolunteers() {
        List<PlatformUser> allUsers = repository.findAll();
        return allUsers.stream()
                .filter(x -> x.getRole().contains("ROLE_VOLUNTEER"))
                .map(this::platformToVolunteerDto)
                .collect(Collectors.toList());
    }

    public List<OrgIndDTO> getAllOrganizations() {
        List<PlatformUser> allUsers = repository.findAll();
        return allUsers.stream()
                .filter(x -> x.getRole().contains("ROLE_ORGANIZATION"))
                .map(this::platformToOrgIndDto)
                .collect(Collectors.toList());
    }

    public List<OrgIndDTO> getAllIndividuals() {
        List<PlatformUser> allUsers = repository.findAll();
        return allUsers.stream()
                .filter(x -> x.getRole().contains("ROLE_INDIVIDUAL"))
                .map(this::platformToOrgIndDto)
                .collect(Collectors.toList());
    }

    public Optional<PlatformUser> getAllDetails(String username) {
        var oUser = repository.findOneByUsername(username);
        if (oUser.isEmpty()) {
            System.out.println("optional empty");
            return Optional.empty();
        }
        var user = oUser.get();
        user.setPassword("** not displayed **");
        return Optional.of(user);
    }

    private VolunteerDTO platformToVolunteerDto(PlatformUser user) {
        var activitiesPending = user.getActivities().stream().filter(x -> x.getStatus().equals("pending")).count();
        var activitiesInProgress = user.getActivities().stream().filter(x -> x.getStatus().equals("in progress")).count();
        var activitiesCompleted = user.getActivities().stream().filter(x -> x.getStatus().equals("completed")).count();

        return new VolunteerDTO(
                user.getUsername(),
                user.getRating(),
                activitiesPending,
                activitiesInProgress,
                activitiesCompleted
                );
    }

    private OrgIndDTO platformToOrgIndDto(PlatformUser user) {
        var activitiesInProgress = user.getActivities().stream().filter(x -> x.getStatus().equals("in progress")).count();
        var activitiesCompleted = user.getActivities().stream().filter(x -> x.getStatus().equals("completed")).count();

        return new OrgIndDTO(
                user.getUsername(),
                user.getRating(),
                activitiesInProgress,
                activitiesCompleted
        );
    }


}
