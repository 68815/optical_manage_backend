package com.optical.manage.dto.user;

import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String password;
    private String phone;
    private String email;
    private String avatarUrl;
}
