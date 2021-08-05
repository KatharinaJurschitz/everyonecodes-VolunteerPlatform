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

    private String username;

    private Set<String> role;
    private String fullname;
    private LocalDate dateOfBirth;
    private String address;
    private String email;
    private String description;


    public PlatformDTO(String fullname, LocalDate dateOfBirth, String address, String email, String description) {
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.description = description;
    }
}
