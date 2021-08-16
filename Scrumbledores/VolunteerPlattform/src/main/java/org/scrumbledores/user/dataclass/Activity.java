package org.scrumbledores.user.dataclass;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Activity {

    @NotEmpty
    @Size(max = 40)
    private String title;
    @NotEmpty
    private String description;
    private String recommendedSkills;
    private List<String> categories;
    @NotEmpty
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status; // drafts, in progress, completed
    private List<Rating> ratings;

}