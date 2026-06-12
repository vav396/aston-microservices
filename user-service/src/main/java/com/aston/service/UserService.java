package com.aston.service;

import com.aston.dto.UserDto;
import com.aston.dto.event.UserEventDto;
import com.aston.dto.event.UserOperation;
import com.aston.entity.User;
import com.aston.exception.UserNotFoundException;
import com.aston.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto) // Преобразуем каждый User в UserDto
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return convertToDto(user);
    }

    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "createUserFallback")
    public UserDto createUser(UserDto userDto) {
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);

        // Отправка события в Kafka
        UserEventDto event = new UserEventDto(UserOperation.CREATE, savedUser.getEmail());
        kafkaTemplate.send("user-events", event);

        return convertToDto(savedUser);
    }

    public UserDto createUserFallback(UserDto userDto, Throwable throwable) {
        log.error("Ошибка при создании пользователя: {}", throwable.getMessage());
        UserDto fallbackDto = new UserDto();
        fallbackDto.setName(userDto.getName());
        fallbackDto.setEmail(userDto.getEmail());
        fallbackDto.setAge(userDto.getAge());
        fallbackDto.setId(-1L);
        return fallbackDto;
    }

    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "updateUserFallback")
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Обновляем поля
        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setAge(userDto.getAge());

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    public UserDto updateUserFallback(Long id, UserDto userDto, Throwable throwable) {
        log.error("Ошибка при обновлении пользователя {}: {}", id, throwable.getMessage());
        UserDto fallbackDto = new UserDto();
        fallbackDto.setId(id);
        fallbackDto.setName(userDto.getName());
        fallbackDto.setEmail(userDto.getEmail());
        fallbackDto.setAge(userDto.getAge());
        return fallbackDto;
    }

    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "deleteUserFallback")
    public void deleteUser(Long id) {
        // Сначала находим пользователя, чтобы получить его email перед удалением
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        String email = user.getEmail();

        // Удаляем пользователя из БД
        userRepository.deleteById(id);

        // Отправка события в Kafka
        UserEventDto event = new UserEventDto(UserOperation.DELETE, email);
        kafkaTemplate.send("user-events", event);
    }

    public void deleteUserFallback(Long id, Throwable throwable) {
        log.error("Ошибка при удалении пользователя {}: {}", id, throwable.getMessage());
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }

    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        return user;
    }
}