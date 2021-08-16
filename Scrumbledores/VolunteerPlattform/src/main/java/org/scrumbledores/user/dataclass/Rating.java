package org.scrumbledores.user.dataclass;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Rating {

    private PlatformUser participant;
    private int ratingFromParticipant;
    private int ratingFromCreator;
    private String feedbackFromParticipant;
    private String feedbackFromCreator;

}