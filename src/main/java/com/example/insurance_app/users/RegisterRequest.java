package com.example.insurance_app.users;

import lombok.Data;
import com.example.insurance_app.users.User.Role;
import jakarta.validation.constraints.NotNull;
@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    @NotNull(message = "Role is required")
    private Role role;

}
