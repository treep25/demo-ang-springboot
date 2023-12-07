package com.demo.backend.user.model;

import com.demo.backend.order.model.Order;
import com.demo.backend.user.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UserDto {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Order order;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private boolean isEnabled;
}
