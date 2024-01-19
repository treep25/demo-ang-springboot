package com.demo.backend.support.messages.repository;

import com.demo.backend.support.messages.model.GroupMessage;
import com.demo.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findAllByDialogUUID(UUID dialogUUID);

    List<GroupMessage> findAllBySender(User sender);

    @Query("SELECT gm FROM GroupMessage gm " +
            "WHERE :recipient MEMBER OF gm.recipients")
    List<GroupMessage> findAllByRecipients(User recipient);

    @Query("SELECT distinct gm FROM GroupMessage gm " +
            "WHERE (gm.sender = :sender AND :recipient MEMBER OF gm.recipients)")
    List<GroupMessage> findGroupMessageBySenderAndRecipient(@Param("sender") User sender,
                                                            @Param("recipient") User recipient);

    @Query("SELECT distinct gm FROM GroupMessage gm " +
            "WHERE (gm.sender = :recipient AND :sender MEMBER OF gm.recipients)")
    List<GroupMessage> findGroupMessageByRecipientAndSender(@Param("sender") User sender,
                                                            @Param("recipient") User recipient);

    @Query("SELECT distinct gm.dialogUUID FROM GroupMessage gm " +
            "WHERE (gm.sender = :sender " +
            "AND :recipient MEMBER OF gm.recipients) ")
    UUID findFirstBySenderAndRecipients(@Param("sender") User sender,
                                        @Param("recipient") User recipient);
}
