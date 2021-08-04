package org.scrumbledores.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicDTO {

    private String username;
    private String fullname;
    private int age;
    private String description;

}