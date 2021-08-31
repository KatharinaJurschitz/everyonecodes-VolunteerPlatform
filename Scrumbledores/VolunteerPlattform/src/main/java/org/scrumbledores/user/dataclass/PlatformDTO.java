package org.scrumbledores.user.dataclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    private String skills;
    @Min(0)
    @Max(5)
    private double rating = 0;
    private int exp;


    public PlatformDTO(String fullname, LocalDate dateOfBirth, String address, String email, String description) {
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.description = description;
    }
}
