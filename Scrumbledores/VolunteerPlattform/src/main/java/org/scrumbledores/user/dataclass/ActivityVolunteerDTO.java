package org.scrumbledores.user.dataclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityVolunteerDTO {

    @NotEmpty
    @Size(max = 40)
    private String title;
    @NotEmpty
    private String description;
    private String status;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private String creatorName;
    private String creatorRole;
    private double creatorRating;
    private int ratingVolunteer;
    private String feedbackVolunteer;
    private int ratingCreator;
    private String feedbackCreator;

}
