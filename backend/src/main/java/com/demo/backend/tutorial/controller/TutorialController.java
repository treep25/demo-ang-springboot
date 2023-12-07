package com.demo.backend.tutorial.controller;

import com.demo.backend.tutorial.model.SearchingTutorialRequest;
import com.demo.backend.tutorial.model.dto.TutorialDto;
import com.demo.backend.tutorial.service.TutorialService;
import com.demo.backend.user.model.User;
import com.demo.backend.utils.DataValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/tutorials")
@RequiredArgsConstructor
@Validated
public class TutorialController {

    private final TutorialService tutorialService;

    @GetMapping
    public ResponseEntity<?> getAllTutorials(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tutorialService.getAllTutorials(user.getRole()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTutorialById(@PathVariable long id) {
        DataValidation.validateInputParamsOrElseThrowException(id);

        return ResponseEntity.ok(tutorialService.getTutorialById(id));
    }

    @PostMapping
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> saveTutorial(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("overview") String overview,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal User user) {

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
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> deleteTutorial(@PathVariable long id, @AuthenticationPrincipal User user) {
        DataValidation.validateInputParamsOrElseThrowException(id);

        tutorialService.deleteTutorialById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> deleteAllTutorials(@AuthenticationPrincipal User user) {
        tutorialService.deleteAllTutorials();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findTutorialByAnyParameter(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sortedType", required = false) String sortedType,
            @RequestParam(value = "status", required = false) String tutorialStatus,
            @AuthenticationPrincipal User user) {

        DataValidation.preUpdateValidationOfFields(title, description, sortedType, tutorialStatus);

        SearchingTutorialRequest searchingTutorialRequest = SearchingTutorialRequest
                .builder()
                .title(title)
                .description(description)
                .sortingType(sortedType)
                .tutorialStatus(tutorialStatus)
                .build();

        return ResponseEntity.ok(tutorialService.findTutorialByDifferentParams(searchingTutorialRequest, user.getRole()));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> updateTutorialById(
            @PathVariable long id,
            @RequestBody TutorialDto tutorialDto,
            @AuthenticationPrincipal User user) {

        DataValidation.validateInputParamsOrElseThrowException(id);

        return ResponseEntity.ok(tutorialService.updateTutorialById(id, tutorialDto));
    }

    @PatchMapping("status/{id}")
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> updateTutorialStatusById(
            @PathVariable long id,
            @RequestBody String status,
            @AuthenticationPrincipal User user) {

        DataValidation.validateInputParamsOrElseThrowException(id);
        DataValidation.validateStatus(status);

        return ResponseEntity.ok(tutorialService.updateTutorialById(id, status));
    }

    @GetMapping("getImage/{id}")
    public ResponseEntity<?> getImage(@PathVariable long id) {
        DataValidation.validateInputParamsOrElseThrowException(id);

        byte[] tutorialImage = tutorialService.getTutorialImage(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(tutorialImage, headers, HttpStatus.OK);
    }
}

