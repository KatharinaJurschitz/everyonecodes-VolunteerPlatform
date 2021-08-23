package org.scrumbledores.user;

import org.scrumbledores.user.dataclass.ActivityDB;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityDBRepository extends MongoRepository<ActivityDB, String> {

    List<ActivityDB> findAllBy(TextCriteria criteria, Sort sort);

}
