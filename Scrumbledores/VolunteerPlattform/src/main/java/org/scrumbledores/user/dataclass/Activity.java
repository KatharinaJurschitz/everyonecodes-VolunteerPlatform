package org.scrumbledores.user.dataclass;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Activity {

    private String activityId = UUID.randomUUID().toString();
    private String creatorName;
    private String creatorRole;
    private double creatorRating;
    @NotEmpty
    @Size(max = 40)
    private String title;
    @NotEmpty
    private String description;
    private String recommendedSkills = "";
    private List<String> categories = new ArrayList<>();
    @NotEmpty
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status; // drafts, in progress, completed
    private LocalDate timestamp;
    private List<Rating> ratings = new ArrayList<>();


    public Activity(String title, String description, LocalDateTime startDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
    }
}