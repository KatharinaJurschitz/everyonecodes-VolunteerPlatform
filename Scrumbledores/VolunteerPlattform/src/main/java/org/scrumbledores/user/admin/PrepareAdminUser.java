package org.scrumbledores.user.admin;

import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.dataclass.PlatformUser;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PrepareAdminUser {

    @Bean
    ApplicationRunner prepareAdminUsers(PlatformUserRepository platformUserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String username = "Admin";
            String fullname = "Admin";
            String email = "admin@admin.com";
            if (!platformUserRepository.existsByUsername(username)) {
                String password = passwordEncoder.encode("secret");
                PlatformUser admin = new PlatformUser(username, password, fullname, email);
                platformUserRepository.save(admin);
            }
        };
    }
}