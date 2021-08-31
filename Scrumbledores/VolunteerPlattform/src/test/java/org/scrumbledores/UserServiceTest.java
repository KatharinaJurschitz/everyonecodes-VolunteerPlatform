package org.scrumbledores;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.scrumbledores.user.dataclass.PlatformDTO;
import org.scrumbledores.user.dataclass.PlatformUser;
import org.scrumbledores.user.PlatformUserRepository;
import org.scrumbledores.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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




    @Test
    void testShowPersonalData() {
        PlatformUser user = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "zeplichalmike@gmail.com", "test");
        PlatformDTO platformDTO = new PlatformDTO(user.getUsername(), user.getRole(), user.getFullname(), user.getDateOfBirth(),
                user.getAddress(), user.getEmail(), user.getDescription(), user.getSkills(), user.getRating(), user.getExp());
        Principal principal = () -> "test";
        Mockito.when(repository.findOneByUsername(principal.getName())).thenReturn(Optional.of(user));
        PlatformDTO result = service.showPersonalData(principal);
        Assertions.assertEquals(platformDTO, result);
        Mockito.verify(repository).findOneByUsername(principal.getName());
    }

    @Test
    void testEditPersonalDataEverythingStaysSame() {
        PlatformUser user = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "test", "test");
        PlatformDTO platformDTO = new PlatformDTO("test",Set.of("ROLE_VOLUNTEER"), "test",LocalDate.now(), "test", "test", "test", "test", 0, 1);
        Principal principal = () -> "test";
        Mockito.when(repository.findOneByUsername(principal.getName())).thenReturn(Optional.of(user));
        PlatformDTO result = service.editPersonalData(platformDTO, principal);
        Assertions.assertEquals(platformDTO, result);
        Mockito.verify(repository).findOneByUsername(principal.getName());

    }

    @Test
    void testEditPersonalDataEmptyUsername() {
        PlatformUser user = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "test", "test");
        PlatformDTO platformDTO = new PlatformDTO("test",Set.of("ROLE_VOLUNTEER"), "",LocalDate.now(), "test", "test", "test", "test", 0,1);
        Principal principal = () -> "test";
        Mockito.when(repository.findOneByUsername(principal.getName())).thenReturn(Optional.of(user));
        PlatformDTO result = service.editPersonalData(platformDTO, principal);
        Assertions.assertEquals(user.getFullname(), result.getFullname());
        Mockito.verify(repository).findOneByUsername(principal.getName());

    }

    @Test
    void testEditPersonalDataNullUsername() {
        PlatformUser user = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "test", "test");
        PlatformDTO platformDTO = new PlatformDTO("test",Set.of("ROLE_VOLUNTEER"), null,LocalDate.now(), "test", "test", "test", "test", 0,1);
        Principal principal = () -> "test";
        Mockito.when(repository.findOneByUsername(principal.getName())).thenReturn(Optional.of(user));
        PlatformDTO result = service.editPersonalData(platformDTO, principal);
        Assertions.assertEquals(user.getFullname(), result.getFullname());
        Mockito.verify(repository).findOneByUsername(principal.getName());

    }

    @Test
    void testEditPersonalDataEmailNotValid() {
        PlatformUser user = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "zeplichalmike@gmail.com", "test");
        PlatformDTO platformDTO = new PlatformDTO("test",Set.of("ROLE_VOLUNTEER"), "",LocalDate.now(), "test", "test", "test", "test", 0,1);
        Principal principal = () -> "test";
        Mockito.when(repository.findOneByUsername(principal.getName())).thenReturn(Optional.of(user));
        PlatformDTO result = service.editPersonalData(platformDTO, principal);
        Assertions.assertEquals(user.getEmail(), result.getEmail());
        Mockito.verify(repository).findOneByUsername(principal.getName());

    }

    @Test
    void testEditPersonalDataEmailNull() {
        PlatformUser user = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "zeplichalmike@gmail.com", "test");
        PlatformDTO platformDTO = new PlatformDTO("test",Set.of("ROLE_VOLUNTEER"), "",LocalDate.now(), "test", null, "test", "test", 0,1);
        Principal principal = () -> "test";
        Mockito.when(repository.findOneByUsername(principal.getName())).thenReturn(Optional.of(user));
        PlatformDTO result = service.editPersonalData(platformDTO, principal);
        Assertions.assertEquals(user.getEmail(), result.getEmail());
        Mockito.verify(repository).findOneByUsername(principal.getName());

    }

    @Test
    void testEditPersonalDataSkillsValid() {
        PlatformUser user = new PlatformUser("test", "test", Set.of("ROLE_VOLUNTEER"), "test", LocalDate.now(), "test", "zeplichalmike@gmail.com", "test");
        PlatformDTO platformDTO = new PlatformDTO("test",Set.of("ROLE_VOLUNTEER"), "",LocalDate.now(), "test", null, "test", "cooking;gardening", 0,1);
        Principal principal = () -> "test";
        Mockito.when(repository.findOneByUsername(principal.getName())).thenReturn(Optional.of(user));
        PlatformDTO result = service.editPersonalData(platformDTO, principal);
        Assertions.assertEquals(platformDTO.getSkills(), result.getSkills());
        Mockito.verify(repository).findOneByUsername(principal.getName());
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
            "gardening, true",
            "gardening; cooking, true",
            "gardening;cooking, true",
            "gardening;; cooking, false",
            ";gardening; cooking, false",
            "gardening; cooking;, false",
            "gardening cooking, true",
            "garden9, false",
            "garden:, false",
    })
    void isSkillsValidTest(String input, boolean expected) {
        var result = service.isSkillsValid(input);
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
