package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
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
}
