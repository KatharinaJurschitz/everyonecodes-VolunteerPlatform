package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.scrumbledores.email.EmailService;
import org.scrumbledores.user.dataclass.PasswordReset;
import org.scrumbledores.user.dataclass.PlatformDTO;
import org.scrumbledores.user.dataclass.PlatformUser;
import org.scrumbledores.user.dataclass.UserPublicDTO;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Setter
@ConfigurationProperties("platformuser")
public class UserService {

    private final PlatformUserRepository repository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
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
                user.getAddress(), user.getEmail(), user.getDescription(), user.getSkills(), user.getRating(), user.getExp());
    }

    public UserPublicDTO showOwnPublicData(Principal principal) {
        var user = findUser(principal);

        return platformUserToUserPublicDTO(user);
    }

    public UserPublicDTO platformUserToUserPublicDTO(PlatformUser user) {

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
                user.getRating(),
                user.getExp()
        );
    }

    public Optional<UserPublicDTO> showOtherUserPublicData(String username, Principal principal) {

        var user = findUser(principal);
        var role = new ArrayList<>(user.getRole()).get(0);
        var oResult = repository.findOneByUsername(username);
        if (oResult.isEmpty()) {
            return Optional.empty();
        }

        var result = oResult.get();
        var resultRole = new ArrayList<>(result.getRole()).get(0);

        switch (role) {
            case "ROLE_VOLUNTEER":
                if (!resultRole.equals("ROLE_VOLUNTEER")) {
                    return Optional.of(platformUserToUserPublicDTO(result));
                }
                break;

            case "ROLE_ORGANIZATION":
                if (resultRole.equals("ROLE_VOLUNTEER")) {
                    return Optional.of(platformUserToUserPublicDTO(result));
                }
                break;

            case "ROLE_INDIVIDUAL":
                if (resultRole.equals("ROLE_VOLUNTEER")) {
                    return Optional.of(platformUserToUserPublicDTO(result));
                }

            default:
                return Optional.empty();
        }
        return Optional.empty();
    }

    public String resetPassword(String username, String password) {
        String token = UUID.randomUUID().toString();
        var oUser = repository.findOneByUsername(username);
        if (oUser.isEmpty()) {
            return "Reset failed";
        }

        var user = oUser.get();

        passwordResetRepository.save(new PasswordReset(token, LocalDateTime.now().plusMinutes(15), encoder.encode(password), user));

        emailService.sendEmail(user.getEmail(), "Password Reset",
                "To reset your password click this link <a href=\"http://localhost:9000/users/profile/password/confirm?password=" + token + "\">click me</a>");
        return "E-Mail with activation link was sent";

    }

    public String resetPasswordConfirm(String confirm) {
        var reset = passwordResetRepository.findByToken(confirm);
        if (reset.isPresent()) {
            if (LocalDateTime.now().isBefore(reset.get().getValidTill())) {
                var user = reset.get().getUser();
                user.setPassword(reset.get().getNewPassword());
                repository.save(user);
                passwordResetRepository.delete(reset.get());
                return "Password updated";
            }
        }
        return "Not Valid";
    }
}
