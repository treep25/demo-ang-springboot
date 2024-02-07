package com.example.grpc.todo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
public class TodoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String whatToDo;
    private String info;
    private LocalDateTime expDate;
    private LocalDateTime startDate;

    public void setExpDate(long seconds) {
        this.expDate = LocalDateTime.now().plus(Duration.ofSeconds(seconds));
    }

    public void setStartDate() {
        this.startDate = LocalDateTime.now();
    }

}
