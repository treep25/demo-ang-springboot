package com.demo.backend.controller;

import com.demo.backend.model.SearchingTutorialRequest;
import com.demo.backend.model.dto.TutorialDto;
import com.demo.backend.service.TutorialService;
import com.demo.backend.utils.DataValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;


@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/tutorials")
@RequiredArgsConstructor
@Validated
public class TutorialController {

    private final TutorialService tutorialService;

    @GetMapping
    public ResponseEntity<?> getAllTutorials() {
        return ResponseEntity.ok(tutorialService.getAllTutorials());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTutorialById(@PathVariable long id) {
        DataValidation.validateInputParamsOrElseThrowException(id);

        return ResponseEntity.ok(tutorialService.getTutorialById(id));
    }

    @PostMapping
    public ResponseEntity<?> saveTutorial(@RequestParam("title") String title,
                                          @RequestParam("description") String description,
                                          @RequestParam("overview") String overview,
                                          @RequestParam("content") String content,
                                          @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        TutorialDto build = TutorialDto
                .builder()
                .image(image)
                .description(description)
                .content(content)
                .overview(overview)
                .title(title)
                .build();
        return ResponseEntity.ok(tutorialService.saveTutorial(build));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTutorial(@PathVariable long id) {
        DataValidation.validateInputParamsOrElseThrowException(id);

        tutorialService.deleteTutorialById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllTutorials() {
        tutorialService.deleteAllTutorials();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findTutorialByTitle(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sortedType", required = false) String sortedType,
            @RequestParam(value = "status", required = false) String tutorialStatus) {

        DataValidation.preUpdateValidationOfFields(title, description, sortedType, tutorialStatus);

        SearchingTutorialRequest searchingTutorialRequest = SearchingTutorialRequest
                .builder()
                .title(title)
                .description(description)
                .sortingType(sortedType)
                .tutorialStatus(tutorialStatus)
                .build();

        return ResponseEntity.ok(tutorialService.findTutorialByDifferentParams(searchingTutorialRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTutorialById(@PathVariable long id, @RequestBody TutorialDto tutorialDto) {
        DataValidation.validateInputParamsOrElseThrowException(id);

        return ResponseEntity.ok(tutorialService.updateTutorialById(id, tutorialDto));
    }

    @PatchMapping("status/{id}")
    public ResponseEntity<?> updateTutorialStatusById(@PathVariable long id, @RequestBody boolean data) {
        DataValidation.validateInputParamsOrElseThrowException(id);

        return ResponseEntity.ok(tutorialService.updateTutorialById(id, data));
    }

    //todo fix it
    @PostMapping("getImage")
    public ResponseEntity<byte[]> getImage(@RequestBody String imagePath) throws IOException {
        imagePath = "backend/images/1700743629279_photo_2023-11-14_11-29-25.jpg";

        ClassPathResource resource = new ClassPathResource(imagePath);

        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}

