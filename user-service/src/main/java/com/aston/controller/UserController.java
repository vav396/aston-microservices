package com.aston.controller;

import com.aston.dto.UserDto;
import com.aston.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    // 1. GET /api/users - Получить всех пользователей
    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей из базы данных с HATEOAS ссылками")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ", content = @Content(schema = @Schema(implementation = UserDto.class)))
    })
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {
        List<EntityModel<UserDto>> users = userService.getAllUsers().stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserDto>> collectionModel = CollectionModel.of(users);
        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    // 2. GET /api/users/{id} - Получить пользователя по ID
    // Добавлено ("id") в @PathVariable для гарантии работы в тестах
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его идентификатору с HATEOAS ссылками")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<EntityModel<UserDto>> getUserById(@PathVariable("id") @Parameter(description = "Идентификатор пользователя") Long id) {
        UserDto user = userService.getUserById(id);

        EntityModel<UserDto> resource = EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return ResponseEntity.ok(resource);
    }

    // 3. POST /api/users - Создать нового пользователя
    @PostMapping
    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя и сохраняет в базу данных")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Пользователь успешно создан", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    public ResponseEntity<EntityModel<UserDto>> createUser(@RequestBody @Parameter(description = "Данные нового пользователя") UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);

        EntityModel<UserDto> resource = EntityModel.of(createdUser,
                linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return ResponseEntity.status(201).body(resource);
    }

    // 4. PUT /api/users/{id} - Обновить пользователя
    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные существующего пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    public ResponseEntity<EntityModel<UserDto>> updateUser(@PathVariable("id") @Parameter(description = "Идентификатор пользователя") Long id, @RequestBody @Parameter(description = "Новые данные пользователя") UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);

        EntityModel<UserDto> resource = EntityModel.of(updatedUser,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return ResponseEntity.ok(resource);
    }

    // 5. DELETE /api/users/{id} - Удалить пользователя
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя из базы данных")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable("id") @Parameter(description = "Идентификатор пользователя") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
