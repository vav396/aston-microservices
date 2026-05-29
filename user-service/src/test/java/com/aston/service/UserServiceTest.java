package com.aston.service;

import com.aston.dto.UserDto;
import com.aston.entity.User;
import com.aston.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Подключает Mockito
public class UserServiceTest {

    @Mock
    private UserRepository userRepository; // Имитация репозитория

    @InjectMocks
    private UserService userService; // Реальный сервис, куда внедряется мок-репозиторий

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Подготовка данных перед каждым тестом
        user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@test.com");
        user.setAge(25);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Alex");
        userDto.setEmail("alex@test.com");
        userDto.setAge(25);
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Alex");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }
}