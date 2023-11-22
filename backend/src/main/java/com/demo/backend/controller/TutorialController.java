package com.demo.backend.controller;

import com.demo.backend.model.SearchingTutorialRequest;
import com.demo.backend.model.dto.TutorialDto;
import com.demo.backend.service.TutorialService;
import com.demo.backend.utils.DataValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<?> saveTutorial(@Valid @RequestBody TutorialDto tutorialDto) {
        return ResponseEntity.ok(tutorialService.saveTutorial(tutorialDto));
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
            @RequestParam(value = "sortedType", required = false) String sortedType) {

//        DataValidation.validateInputParamsOrElseThrowException(title);

        SearchingTutorialRequest searchingTutorialRequest = SearchingTutorialRequest
                .builder()
                .title(title)
                .description(description)
                .sortingType(sortedType)
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
}

