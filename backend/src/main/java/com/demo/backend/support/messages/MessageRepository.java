package com.demo.backend.support.messages;

import com.demo.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByDialogUUID(UUID dialogUUID);

    @Query("SELECT DISTINCT m.dialogUUID FROM Message m " +
            "WHERE (m.sender = :sender AND m.recipient = :recipient) " +
            "   OR (m.sender = :recipient AND m.recipient = :sender)")
    Optional<UUID> findDialogUUIDBySenderAndRecipient(@Param("sender") User sender,
                                                      @Param("recipient") User recipient);
}
