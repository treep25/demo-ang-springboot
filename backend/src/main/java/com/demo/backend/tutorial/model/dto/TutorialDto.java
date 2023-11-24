package com.demo.backend.tutorial.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class TutorialDto {

    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 100, message = "Description must be between 5 and 100 characters")
    private String description;
    private String overview;
    private String content;
    private MultipartFile image;
}
