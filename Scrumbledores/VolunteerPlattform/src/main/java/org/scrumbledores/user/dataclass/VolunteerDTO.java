package org.scrumbledores.user.dataclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDTO {

    private String username;
    private double rating;
    private long activitiesPending;
    private long activitiesInProgress;
    private long activitiesCompleted;
}
