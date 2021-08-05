package org.scrumbledores;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.scrumbledores.user.PlatformUser;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceTest {
    @Autowired
    UserService service;

    @MockBean
    PlatformUserRepository repository;


    @Test
    void testCreateUserSuccessful() {
        PlatformUser platformUser = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "scrumbledore.email@gmail.com", "test");
        Mockito.when(repository.save(platformUser)).thenReturn(platformUser);
        Optional<PlatformUser> result = service.createUser(platformUser);
        Assertions.assertEquals(platformUser, result.get());
        Mockito.verify(repository).save(platformUser);
    }

    @Test
    void testCreateUserFailBecauseEmail() {
        PlatformUser platformUser = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "testemail.com", "test");
        Optional<PlatformUser> result = service.createUser(platformUser);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testCreateUserFailBecauseUsername() {
        PlatformUser platformUser = new PlatformUser("test!!", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "scrumbledore.email@gmail.com", "test");
        Optional<PlatformUser> result = service.createUser(platformUser);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testCreateUserFailBecauseRoleSizeMoreThanOne() {
        PlatformUser platformUser = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER", "ROLE_INDIVIDUAL"), "test", LocalDate.now(), "test", "scrumbledore.email@gmail.com", "test");
        Optional<PlatformUser> result = service.createUser(platformUser);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testCreateUserFailBecauseRoleSizeEmpty() {
        PlatformUser platformUser = new PlatformUser("test", "test", Set.of(), "test", LocalDate.now(), "test", "scrumbledore.email@gmail.com", "test");
        Optional<PlatformUser> result = service.createUser(platformUser);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testCreateUserFailBecauseFalseRole() {
        PlatformUser platformUser = new PlatformUser("test", "test", Set.of("ROLE_TEST"), "test", LocalDate.now(), "test", "scrumbledore.email@gmail.com", "test");
        Optional<PlatformUser> result = service.createUser(platformUser);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindUserSuccess() {
        PlatformUser platformUser = new PlatformUser("test", "test", Set.of("ROLE_TEST"), "test", LocalDate.now(), "test", "test", "test");
        Principal principal = mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test");
        Mockito.when(repository.findOneByUsername("test")).thenReturn(Optional.of(platformUser));
        PlatformUser result = service.findUser(principal);
        Assertions.assertEquals(platformUser, result);
        Mockito.verify(principal).getName();
        Mockito.verify(repository).findOneByUsername("test");
    }
    

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

    @ParameterizedTest
    @CsvSource({
            "user, true",
            "u, true",
            "user1, true",
            "user1user, true",
            "1user1, true",
            "1, true",
            "1user, true",
            "user?, false",
            "?, false",
            "user?user, false",
            "?user, false",
            "1?user, false",
            "user?1, false",
            "1?1r, false",
            "?1, false",
    })
    void isUsernameValidTest(String input, boolean expected) {
        var result = service.isUsernameValid(input);
        Assertions.assertEquals(expected, result);
    }
}
