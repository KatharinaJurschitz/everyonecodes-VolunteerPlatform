package org.scrumbledores;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceTest {
    @Autowired
    UserService service;

    PlatformUserRepository repository;
    PasswordEncoder encoder;
    Set<String> roles;

    @ParameterizedTest
    @CsvSource({
            "email@email.com, true",
            "email@com, false",
            "@email.com, false",
            "email@email, false",
            "email@emailcom, false",
    })
    void isEmailValidTest(String input, boolean expected) {
        var result = service.isEmailValid(input);
        Assertions.assertEquals(expected, result);
    }
}
