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

// @WebMvcTest загружает только слой Web (контроллеры), игнорируя сервисы и БД
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Имитация HTTP-клиента

    @MockBean
    private UserService userService; // Имитация сервиса

    @Autowired
    private ObjectMapper objectMapper; // Для преобразования объектов в JSON и обратно

    @Test
    public void testGetAllUsers() throws Exception {
        // 1. Подготовка данных (Mocking)
        UserDto user1 = new UserDto(1L, "Alex", "alex@test.com", 25);
        UserDto user2 = new UserDto(2L, "Ivan", "ivan@test.com", 30);
        List<UserDto> users = Arrays.asList(user1, user2);

        // Говорим Mockito: когда вызовут getAllUsers, верни наш список
        when(userService.getAllUsers()).thenReturn(users);

        // 2. Выполнение запроса
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alex"));
    }

    @Test
    public void testCreateUser() throws Exception {
        // 1. Подготовка данных
        UserDto newUser = new UserDto(null, "NewUser", "new@test.com", 20);
        UserDto savedUser = new UserDto(1L, "NewUser", "new@test.com", 20);

        // Говорим Mockito: когда вызовут createUser с любым UserDto, верни savedUser
        when(userService.createUser(any(UserDto.class))).thenReturn(savedUser);

        // 2. Выполнение запроса
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser))) // Превращаем объект в JSON строку
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("NewUser"));
    }

    @Test
    public void testGetUserById() throws Exception {
        // 1. Подготовка
        UserDto user = new UserDto(1L, "Alex", "alex@test.com", 25);
        when(userService.getUserById(eq(1L))).thenReturn(user);

        // 2. Запрос
        mockMvc.perform(get("/api/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        // Просто проверяем, что метод вызывается и возвращается 204
        mockMvc.perform(delete("/api/users/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}