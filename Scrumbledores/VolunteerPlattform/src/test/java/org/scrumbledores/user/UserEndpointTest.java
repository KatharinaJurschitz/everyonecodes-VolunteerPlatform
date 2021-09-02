package org.scrumbledores.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.scrumbledores.user.dataclass.PlatformUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserEndpointTest {

    @Autowired
    UserEndpoint userEndpoint;

    @Autowired
    TestRestTemplate testRestTemplate;

    @MockBean
    UserService userService;

    String url = "/users";

    @Test
    void testThrow() {
        PlatformUser platformUser = new PlatformUser("test", "test", Set.of("test", "test2"), "test", LocalDate.now(), "test", "test", "test");
        Assertions.assertThrows(ResponseStatusException.class, () -> userEndpoint.create(platformUser));

    }

    @Test
    void userCreated() {
        PlatformUser platformUser = new PlatformUser("test", "test", Set.of("test"), "test", LocalDate.now(), "test", "test", "test");
        Mockito.when(userService.createUser(platformUser)).thenReturn(Optional.of(platformUser));
        testRestTemplate.postForObject(url, platformUser, PlatformUser.class);
        Mockito.verify(userService).createUser(platformUser);
    }




}