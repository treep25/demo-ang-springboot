package com.demo.backend.user.listener;

import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserOauthTokenCleanupListener {

    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 5 * 3600000)
    public void cleanupTable() {
        log.debug("Finding users with tokens scheduled method");

        List<User> allUsersByOauth2TokenIfExist = userRepository.findAllUsersByOauth2TokenIfExist();
        log.debug("Find {} users", allUsersByOauth2TokenIfExist.size());

        allUsersByOauth2TokenIfExist
                .forEach(
                        user -> {
                            user.setAzureAccessTokenNotRequered(null);
                            user.setGoogleAccessTokenNotRequered(null);
                        }
                );

        log.debug("Saving update users");
        userRepository.saveAll(allUsersByOauth2TokenIfExist);
    }

}
