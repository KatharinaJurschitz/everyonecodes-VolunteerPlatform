package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDTO {
    private String id;
    private String username;
    private String password;
    private Set<String> role;
    private String fullname;
    private LocalDate dateOfBirth;
    private String address;
    private String email;
    private String description;

    public PlatformDTO(String username, String password, Set<String> role, String fullname, LocalDate dateOfBirth, String address, String email, String description) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.description = description;
    }

    public PlatformDTO(String username, Set<String> role, String fullname, LocalDate dateOfBirth, String address, String email, String description) {
        this.username = username;
        this.role = role;
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.description = description;
    }

    public PlatformDTO(String fullname, LocalDate dateOfBirth, String address, String email, String description) {
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.description = description;
    }
}
