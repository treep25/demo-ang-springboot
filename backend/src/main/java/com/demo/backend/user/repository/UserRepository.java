package com.demo.backend.user.repository;

import com.demo.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);

    @Query("SELECT u FROM User u WHERE u.isEnabled = true")
    List<User> findAllByIsEmableTrue();

    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    List<User> findAllByEmailIn(List<String> email);

    @Query("SELECT u FROM User u WHERE u.googleAccessTokenNotRequered IS NOT NULL OR u.azureAccessTokenNotRequered IS NOT NULL")
    List<User> findAllUsersByOauth2TokenIfExist();
}
