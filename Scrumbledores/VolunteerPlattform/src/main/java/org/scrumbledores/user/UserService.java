package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.scrumbledores.user.dataclass.PlatformDTO;
import org.scrumbledores.user.dataclass.PlatformUser;
import org.scrumbledores.user.dataclass.UserPublicDTO;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
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
                if (!input.getRole().stream().findFirst().get().equals("ROLE_VOLUNTEER")) {
                    input.setSkills(null);
                }
                return Optional.of(repository.save(input));
            }
        }

        return Optional.empty();
    }

    public PlatformUser findUser(Principal principal) {
        Optional<PlatformUser> user = repository.findOneByUsername(principal.getName());
        return user.get();
    }

    public PlatformDTO findUserDTO(Principal principal) {
        Optional<PlatformUser> user = repository.findOneByUsername(principal.getName());
        return platformUserToDto(user.get());
    }

    public boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        return pattern.matcher(email).matches();
    }

    public boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        return pattern.matcher(username).matches();
    }

    public boolean isSkillsValid(String skills) {
        Pattern pattern = Pattern.compile("^([a-zA-Z ]+;?[a-zA-Z ]*)*[a-zA-Z]+$");
        return pattern.matcher(skills).matches();
    }

    public PlatformDTO showPersonalData(Principal principal) {
        var user = findUser(principal);
        return platformUserToDto(user);
    }

    public PlatformDTO editPersonalData(PlatformDTO dto, Principal principal) {
        var user = findUser(principal);
        if (dto.getFullname() != null && !dto.getFullname().isEmpty()) {
            user.setFullname(dto.getFullname());
        }
        if (dto.getEmail() != null && isEmailValid(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (isSkillsValid(dto.getSkills()) && user.getRole().stream().findFirst().get().equals("ROLE_VOLUNTEER")) {
            user.setSkills(dto.getSkills());
        }

        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setDescription(dto.getDescription());
        repository.save(user);
        return platformUserToDto(user);
    }

    private PlatformUser dtoToPlatformUser(PlatformDTO dto) {
        return new PlatformUser(dto.getUsername(), dto.getRole(), dto.getFullname(), dto.getDateOfBirth(),
                dto.getAddress(), dto.getEmail(), dto.getDescription());
    }

    private PlatformDTO platformUserToDto(PlatformUser user) {
        return new PlatformDTO(user.getUsername(), user.getRole(), user.getFullname(), user.getDateOfBirth(),
                user.getAddress(), user.getEmail(), user.getDescription(), user.getSkills(), user.getRating());
    }

    public UserPublicDTO showOwnPublicData(Principal principal) {
        var user = findUser(principal);

        int age;

        if (user.getDateOfBirth() != null) {
            Period p = Period.between(user.getDateOfBirth(), LocalDate.now());
            age = p.getYears();
        } else age = 0;

        return new UserPublicDTO(
                user.getUsername(),
                user.getFullname(),
                age,
                user.getDescription(),
                user.getSkills(),
                user.getRating()
        );
    }
}
