package com.demo.backend.auth.permission;

import com.demo.backend.user.Role;
import com.demo.backend.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class PermissionCheck {
    public boolean hasPermission(User user) {
        return user.is(Role.ADMIN);
    }
}