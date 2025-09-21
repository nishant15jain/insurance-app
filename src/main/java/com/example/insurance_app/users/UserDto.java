package com.example.insurance_app.users;

import com.example.insurance_app.users.User.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
