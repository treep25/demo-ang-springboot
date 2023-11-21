package com.demo.backend.controller;

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
//    private final Map<Predicate<String>, BiFunction<String, TutorialService, List<Tutorial>>> searchingMap = new HashMap<>(){{
//        put(value -> value != null && !value.isEmpty() && !value.isBlank(), (inputParam, service) -> service);
//    }};


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
            @RequestParam(value = "sorting_type", required = false) String sortedTypeTitle) {
        DataValidation.validateInputParamsOrElseThrowException(title);


        return ResponseEntity.ok(tutorialService.findTutorialByTitle(title));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTutorialById(@PathVariable long id, @RequestBody TutorialDto tutorialDto) {
        DataValidation.validateInputParamsOrElseThrowException(id);

        return ResponseEntity.ok(tutorialService.updateTutorialById(id, tutorialDto));
    }
}

