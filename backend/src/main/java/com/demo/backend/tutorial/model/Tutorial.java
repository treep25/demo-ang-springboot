package com.demo.backend.tutorial.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    private Status status;
    @NotBlank(message = "Overview is required")
    private String overview;
    @NotBlank(message = "Content is required")
    private String content;
    private String imagePath;
}
