package com.demo.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Tutorial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String description;
    private boolean published;
}
