package com.aston.controller;

import com.aston.dto.UserDto;
import com.aston.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllUsers() throws Exception {
        UserDto user1 = new UserDto(1L, "Alex", "alex@test.com", 25);
        UserDto user2 = new UserDto(2L, "Ivan", "ivan@test.com", 30);
        List<UserDto> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alex"));
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDto newUser = new UserDto(null, "NewUser", "new@test.com", 20);
        UserDto savedUser = new UserDto(1L, "NewUser", "new@test.com", 20);

        when(userService.createUser(any(UserDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("NewUser"));
    }

    @Test
    public void testGetUserById() throws Exception {
        UserDto user = new UserDto(1L, "Alex", "alex@test.com", 25);
        when(userService.getUserById(eq(1L))).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        // В @WebMvcTest мы просто проверяем HTTP слой.
        // Логика удаления и отправки в Kafka тестируется в UserServiceTest
        mockMvc.perform(delete("/api/users/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}