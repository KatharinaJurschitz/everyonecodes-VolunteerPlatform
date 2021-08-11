package org.scrumbledores.user.dataclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class PasswordReset {

    private String id;
    private String token;
    private LocalDateTime validTill;
    private String newPassword;
    private PlatformUser user;

    public PasswordReset(String token, LocalDateTime validTill, String newPassword, PlatformUser user) {
        this.token = token;
        this.validTill = validTill;
        this.newPassword = newPassword;
        this.user = user;
    }
}
