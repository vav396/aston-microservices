package com.aston.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDto {
    private UserOperation operation;  // "CREATE" или "DELETE"
    private String email;
}