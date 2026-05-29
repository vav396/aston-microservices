package com.aston.service;

import com.aston.dto.UserDto;
import com.aston.dto.event.UserEventDto;
import com.aston.dto.event.UserOperation;
import com.aston.entity.User;
import com.aston.exception.UserNotFoundException;
import com.aston.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    // --- Методы для работы с DTO ---

    // 1. Получить всех пользователей
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto) // Преобразуем каждый User в UserDto
                .collect(Collectors.toList());
    }

    // 2. Получить пользователя по ID
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return convertToDto(user);
    }

    // 3. Создать нового пользователя
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);

        // Отправка события в Kafka
        UserEventDto event = new UserEventDto(UserOperation.CREATE, savedUser.getEmail());
        kafkaTemplate.send("user-events", event);

        return convertToDto(savedUser);
    }

    // 4. Обновить пользователя
    @Transactional
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

    // 5. Удалить пользователя
    @Transactional
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

    // --- Вспомогательные методы конвертации  ---

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