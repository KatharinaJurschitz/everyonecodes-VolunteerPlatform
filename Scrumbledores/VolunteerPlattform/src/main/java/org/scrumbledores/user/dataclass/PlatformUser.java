package org.scrumbledores.user.dataclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class PlatformUser {

    private String id;
    @Indexed(unique = true)
    @NotEmpty(message = "Username has to be entered")
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private Set<String> role;
    @NotEmpty
    private String fullname;
    private LocalDate dateOfBirth;
    private String address;
    @Indexed(unique = true)
    @NotEmpty
    private String email;
    private String description;
    private int failedLoginAttempt = 0;
    private String skills;
    @Min(0)
    @Max(5)
    private double rating = 0;
    private List<Activity> activities = new ArrayList<>();
    private List<String> notifications = new ArrayList<>();

    public PlatformUser(String username, String password, Set<String> role, String fullname, LocalDate dateOfBirth, String address, String email, String description) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.description = description;
    }

    public PlatformUser(String username, Set<String> role, String fullname, LocalDate dateOfBirth, String address, String email, String description) {
        this.username = username;
        this.role = role;
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.description = description;
    }

    public PlatformUser(String username, String password, String fullname, String email) {
        this.username = username;
        this.password = password;
        this.role = Set.of("ROLE_ADMIN");
        this.fullname = fullname;
        this.email = email;
    }
}
