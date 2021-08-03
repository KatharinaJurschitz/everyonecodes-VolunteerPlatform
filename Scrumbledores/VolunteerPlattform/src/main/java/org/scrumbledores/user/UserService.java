package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Setter
@ConfigurationProperties("platformuser")
public class UserService {

    private final PlatformUserRepository repository;
    private final PasswordEncoder encoder;
    private Set<String> roles;

    public Optional<PlatformUser> createUser(PlatformUser input) {

        if (input.getRole().size() == 1) {

            if (roles.containsAll(input.getRole())) {
                String passwordEncoded = encoder.encode(input.getPassword());
                input.setPassword(passwordEncoded);
                return Optional.of(repository.save(input));
            }
        }

        return Optional.empty();
    }

    public PlatformUser findUser(Principal principal) {
        Optional<PlatformUser> user = repository.findOneByUsername(principal.getName());
        return user.get();
    }
}
