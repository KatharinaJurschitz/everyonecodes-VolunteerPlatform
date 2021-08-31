package org.scrumbledores.user.dataclass;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ActivityDB {

    private String id;
    private String activityId;
    private String creatorName;
    private String creatorRole;
    private double creatorRating;
    @TextIndexed
    private String title;
    @TextIndexed
    private String description;
    @TextIndexed
    private String recommendedSkills = "";
    private List<String> categories = new ArrayList<>();
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status; // drafts, in progress, completed
    private List<Rating> ratings = new ArrayList<>();
    @TextScore
    Float score;
    private LocalDate timestamp;

    public ActivityDB(String activityId, String creatorName, String creatorRole, double creatorRating, String title, String description, String recommendedSkills, List<String> categories, LocalDateTime startDate, String status, List<Rating> ratings, LocalDate timestamp) {
        this.activityId = activityId;
        this.creatorName = creatorName;
        this.creatorRole = creatorRole;
        this.creatorRating = creatorRating;
        this.title = title;
        this.description = description;
        this.recommendedSkills = recommendedSkills;
        this.categories = categories;
        this.startDate = startDate;
        this.status = status;
        this.ratings = ratings;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Activity-ID: " + activityId +
                ", CreatorName: " + creatorName +
                ", CreatorRating: " + creatorRating +
                ", Title: " + title +
                ", Description: " + description +
                ", RecommendedSkills: " + recommendedSkills +
                ", Categories: " + categories +
                ", StartDate: " + startDate +
                ", EndDate: " + endDate +
                ", Status: " + status;
    }
}

