package com.aston.repository;

import com.aston.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Загружает только JPA компоненты
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        // Создаем пользователя
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(30);

        // Сохраняем через EntityManager (имитируем persist)
        User savedInDb = entityManager.persistFlushFind(user);

        // Ищем через наш Репозиторий
        User found = userRepository.findById(savedInDb.getId()).orElse(null);

        // Проверяем
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test User");
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }
}