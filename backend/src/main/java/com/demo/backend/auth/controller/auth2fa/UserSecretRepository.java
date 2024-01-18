package com.demo.backend.auth.controller.auth2fa;

import com.demo.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSecretRepository extends JpaRepository<UserSecret, Long> {
    Optional<UserSecret> findByUser(User user);

    Optional<UserSecret> findBySecretKey(String secret);
}
