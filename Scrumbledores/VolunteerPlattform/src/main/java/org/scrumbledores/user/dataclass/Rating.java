package org.scrumbledores.user.dataclass;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@NoArgsConstructor
public class Rating {

    @DBRef
    private PlatformUser participant;
    private int ratingFromParticipant;
    private int ratingFromCreator;
    private String feedbackFromParticipant;
    private String feedbackFromCreator;

    public Rating(PlatformUser participant) {
        this.participant = participant;
    }
}