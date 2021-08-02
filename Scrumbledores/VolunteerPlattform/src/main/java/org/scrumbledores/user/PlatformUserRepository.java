package org.scrumbledores.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PlatformUserRepository extends MongoRepository<PlatformUser, String> {

    boolean existsByUsername(String username);

    Optional<PlatformUser> findOneByUsername(String username);

}
