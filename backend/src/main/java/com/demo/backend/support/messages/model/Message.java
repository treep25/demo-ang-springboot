package com.demo.backend.support.messages.model;

import com.demo.backend.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID dialogUUID;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime sentDateTime;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", dialogUUID=" + dialogUUID +
                ", content='" + content + '\'' +
                ", sender=" + sender.getUsername() +
                ", recipient=" + recipient.getUsername() +
                ", sentDateTime=" + sentDateTime +
                '}';
    }
}
