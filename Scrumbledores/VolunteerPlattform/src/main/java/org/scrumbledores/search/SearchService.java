package org.scrumbledores.search;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.dataclass.Activity;
import org.scrumbledores.user.dataclass.PlatformUser;
import org.scrumbledores.user.dataclass.SearchUserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchService {

    private final PlatformUserRepository repository;

    public List<Activity> findAllActivities() {
        List<PlatformUser> allUsers = repository.findAll();
        return allUsers.stream()
                .filter(x -> !x.getRole().contains("ROLE_VOLUNTEER"))
                .map(PlatformUser::getActivities)
                .flatMap(List::stream)
                .filter(x -> x.getStatus().equals("in progress"))
                .collect(Collectors.toList());

    }

    public List<SearchUserDTO> findAllVolunteers() {
        List<PlatformUser> allUsers = repository.findAll();
        return allUsers.stream()
                .filter(x -> x.getRole().contains("ROLE_VOLUNTEER"))
                .map(this::platformUserToSearchUserDTO)
                .collect(Collectors.toList());
    }

    private SearchUserDTO platformUserToSearchUserDTO(PlatformUser user) {
        return new SearchUserDTO(
                user.getUsername(),
                user.getSkills(),
                user.getRating()
        );
    }
}
