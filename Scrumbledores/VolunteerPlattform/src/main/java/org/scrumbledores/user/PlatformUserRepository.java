package org.scrumbledores.user;

import org.scrumbledores.user.dataclass.PlatformUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PlatformUserRepository extends MongoRepository<PlatformUser, String> {

    boolean existsByUsername(String username);

    Optional<PlatformUser> findOneByUsername(String username);

    List<PlatformUser> findOneByActivitiesActivityId(String activityId);

    List<PlatformUser> findAllBy(TextCriteria criteria, Sort sort);

    Optional<PlatformUser> findByUnsubscribeId(String unsubscribeId);
}
