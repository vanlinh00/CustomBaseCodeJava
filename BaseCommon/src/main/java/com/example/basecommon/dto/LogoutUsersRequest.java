package com.example.basecommon.dto;

import lombok.Data;


import jakarta.validation.constraints.NotEmpty;
import java.util.List;


@Data
public class LogoutUsersRequest {
    @NotEmpty(message = "User IDs must not be empty")
    private List<Long> userIds;
    private Boolean isAdmin = false;
}

