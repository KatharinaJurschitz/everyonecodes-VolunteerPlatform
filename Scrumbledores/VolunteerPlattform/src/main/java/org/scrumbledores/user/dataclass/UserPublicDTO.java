package org.scrumbledores.user.dataclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicDTO {

    private String username;
    private String fullname;
    private int age;
    private String description;
    private String skills;
    @Min(0)
    @Max(5)
    private double rating = 0;
    private int exp;

}