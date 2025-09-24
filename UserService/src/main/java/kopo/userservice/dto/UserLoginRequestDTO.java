package kopo.userservice.dto;

import lombok.Data;

@Data
public class UserLoginRequestDTO {
    private String userId;
    private String password;
}

