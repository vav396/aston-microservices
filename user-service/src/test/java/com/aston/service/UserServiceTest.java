package com.aston.service;

import com.aston.dto.UserDto;
import com.aston.dto.event.UserEventDto;
import com.aston.entity.User;
import com.aston.exception.UserNotFoundException;
import com.aston.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaTemplate<String, UserEventDto> kafkaTemplate; // Мок для Kafka

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@test.com");
        user.setAge(25);

        userDto = new UserDto();
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
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testCreateUser_Success() {
        // Настраиваем мок репозитория
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Вызываем метод
        UserDto result = userService.createUser(userDto);

        // Проверяем результат
        assertThat(result.getId()).isEqualTo(1L);

        // Проверяем, что репозиторий был вызван
        verify(userRepository, times(1)).save(any(User.class));

        //  Проверяем, что KafkaTemplate был вызван для отправки события CREATE
        verify(kafkaTemplate, times(1)).send(eq("user-events"), any(UserEventDto.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Сначала находим пользователя, чтобы взять email
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Затем удаляем
        doNothing().when(userRepository).deleteById(1L);

        // Вызываем метод
        userService.deleteUser(1L);

        // Проверяем, что удаление произошло
        verify(userRepository, times(1)).deleteById(1L);

        //  Проверяем, что KafkaTemplate был вызван для отправки события DELETE
        verify(kafkaTemplate, times(1)).send(eq("user-events"), any(UserEventDto.class));
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);

        // Убедимся, что в Kafka ничего не ушло, если пользователь не найден
        verify(kafkaTemplate, never()).send(anyString(), any());
    }
}