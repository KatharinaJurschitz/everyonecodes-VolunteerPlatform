package org.scrumbledores.user.dataclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicDTO {

    private String username;
    private String fullname;
    private int age;
    private String description;
    private String skills;

}