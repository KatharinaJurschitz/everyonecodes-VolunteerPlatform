package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Setter
@ConfigurationProperties("platformuser")
public class UserService {

    private final PlatformUserRepository repository;
    private final PasswordEncoder encoder;
    private Set<String> roles;

    public Optional<PlatformUser> createUser(PlatformUser input) {

        if (input.getRole().size() == 1 && isUsernameValid(input.getUsername()) && isEmailValid(input.getEmail())) {

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



    public boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        return pattern.matcher(email).matches();
    }

    public boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        return pattern.matcher(username).matches();
    }
}
