package org.scrumbledores.search;

import lombok.AllArgsConstructor;
import org.scrumbledores.user.ActivityDBRepository;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.scrumbledores.user.dataclass.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchService {

    private final PlatformUserRepository repository;
    private final ActivityDBRepository activityDBRepository;
    private final UserService userServiceFind;

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


    private ActivityDB activityToActivityDB(Activity activity) {
        var activityDB = new ActivityDB(activity.getActivityId(),
                activity.getCreatorName(),
                activity.getCreatorRole(),
                activity.getCreatorRating(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getRecommendedSkills(),
                activity.getCategories(),
                activity.getStartDate(),
                activity.getStatus(),
                activity.getRatings(),
                activity.getTimestamp());
        if (activity.getEndDate() != null) {
            activityDB.setEndDate(activity.getEndDate());
        }
        return activityDB;
    }


    public List<UserPublicDTO> findAllVolunteersFiltered(String searchCriteria, String filterSkills, String filterRating) {
        Sort sort = Sort.by("score");
        TextCriteria criteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matching(searchCriteria);
        List<PlatformUser> users = repository.findAllBy(criteria, sort);
        return users.stream()
                .filter(x -> x.getRole().contains("ROLE_VOLUNTEER"))
                .map(userServiceFind::platformUserToUserPublicDTO)
                .filter(x -> x.getSkills().contains(filterSkills))
                .filter(x -> x.getRating() >= Double.parseDouble(filterRating))
                .collect(Collectors.toList());
    }

    public List<ActivityDB> findAllActivitiesByKeyword(String searchCriteria) {
        activityDBRepository.deleteAll();
        Sort sort = Sort.by("score");
        TextCriteria criteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matching(searchCriteria);
        List<PlatformUser> users = repository.findAll();
        users.stream()
                .map(PlatformUser::getActivities)
                .flatMap(List::stream)
                .map(this::activityToActivityDB)
                .forEach(activityDBRepository::save);
        return activityDBRepository.findAllBy(criteria, sort);
    }

    public List<ActivityDB> findAllActivitiesFiltered(String searchCriteria, String filterDate, String filterCategory, String filterSkills, String filterCreator, String filterRating) {
        var result = findAllActivitiesByKeyword(searchCriteria);

        var streamResult = result.stream()
                .filter(x -> !x.getCreatorRole().equals("ROLE_VOLUNTEER"))
                .filter(x -> x.getStartDate().isAfter(LocalDate.parse(filterDate).atStartOfDay()))
                .filter(x -> x.getCategories().stream().allMatch(a -> a.contains(filterCategory)))
                .filter(x -> x.getRecommendedSkills().contains(filterSkills))
                .filter(x -> x.getCreatorName().contains(filterCreator))
                .filter(x -> x.getCreatorRating() >= Double.parseDouble(filterRating))
                .collect(Collectors.toList());

        List<ActivityDB> categories = new ArrayList<>();
        if (!filterCategory.isEmpty()) {
            for (ActivityDB activityDB : streamResult) {
                for (String category : activityDB.getCategories()) {
                    if (category.contains(filterCategory)) {
                        categories.add(activityDB);
                    }
                }
            }
        }
        else {
            return streamResult;
        }
        return categories;
    }
}
