package org.scrumbledores.user.dataclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    @NotEmpty
    private String username;
    @Min(1)
    @Max(5)
    private int rating;
    private String feedback;
}
