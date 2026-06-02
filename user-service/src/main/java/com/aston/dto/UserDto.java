package com.aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для представления пользователя")
public class UserDto {

    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван Иванов", required = true)
    private String name;

    @Schema(description = "Email пользователя", example = "ivan@example.com", required = true)
    private String email;

    @Schema(description = "Возраст пользователя", example = "25", minimum = "0")
    private int age;

}
