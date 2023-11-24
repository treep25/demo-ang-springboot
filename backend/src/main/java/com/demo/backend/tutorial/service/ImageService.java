package com.demo.backend.tutorial.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${upload.path}")
    private String uploadPath;

    public Path uploadImage(MultipartFile image) {
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(uploadPath, fileName);

        try {
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error appears during saving the image");
        }
        return filePath;
    }

    public byte[] downloadImage(String imagePath) {
        Path absolutePath = Paths.get(imagePath);

        try {
            return Files.readAllBytes(absolutePath);
        } catch (IOException e) {
            throw new RuntimeException("Error appears during getting the image");
        }
    }
}
