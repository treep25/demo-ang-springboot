package com.demo.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 100, message = "Description must be between 5 and 100 characters")
    private String description;
    private boolean published;
}
