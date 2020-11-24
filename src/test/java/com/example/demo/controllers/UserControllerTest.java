package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.example.demo.controllers.CartControllerTest.emptyCart;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        this.userController = new UserController();
        TestUtils.injectObject(userController,"userRepository",userRepository);
        TestUtils.injectObject(userController,"cartRepository",cartRepository);
        TestUtils.injectObject(userController,"bCryptPasswordEncoder",encoder);
    }

    @Test
    public void createUserTest() throws Exception {
        CreateUserRequest r = createUserRequest("test","testpassword");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(Long.valueOf("0"),u.getId());
        assertEquals("test",u.getUsername());
        assertEquals(encoder.encode("testpassword"),u.getPassword());
    }

    @Test
    public void getUserById(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
    }

    @Test
    public void getUserByName(){
        when(userRepository.findByUsername("test")).thenReturn(createUser());
        ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
    }

    @Test
    public void createEmptyUser(){
        CreateUserRequest r = createUserRequest("","");
        ResponseEntity<User> responseEntity = userController.createUser(r);
        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    //region helper functions for tests
    public static CreateUserRequest createUserRequest(String username,String password) {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername(username);
        user.setPassword(password);
        user.setConfirmPassword(password);
        return user;
    }
    public static User createUser(){
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testpassword");
        user.setCart(emptyCart());
        return user;
    }
    //endregion

}
