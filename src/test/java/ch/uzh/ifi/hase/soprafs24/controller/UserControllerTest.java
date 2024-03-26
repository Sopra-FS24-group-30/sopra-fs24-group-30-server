package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_UsernameExists() throws Exception {

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test existingUser");
        userPostDTO.setUsername("testExistingUsername");

        given(userService.createUser(any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "The username and the name provided are not unique. Therefore, the user could not be created!"));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO))
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void profile_getUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");

        given(userService.profile(any())).willReturn(user);

        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profile/{userid}", user.getId())
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    public void profile_getUser_notExist() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");

        given(userService.profile(any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User could not be found"));

        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profile/{userid}", 2)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_success() throws Exception {
        //given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");

        //no given because it doesn't return anything

        MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/profile/{userid}/edit", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateUser_Fail() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");

        given(userService.edit(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User could not be found"));

        MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/profile/{userid}/edit", 4)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());

    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}