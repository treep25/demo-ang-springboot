package com.demo.backend.auth.controller.auth2fa;

import com.demo.backend.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
public class UserSecret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String secretKey;
}