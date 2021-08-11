package org.scrumbledores.user;

import org.scrumbledores.user.dataclass.PasswordReset;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetRepository extends MongoRepository<PasswordReset, String> {

    Optional<PasswordReset> findByToken (String token);
}
