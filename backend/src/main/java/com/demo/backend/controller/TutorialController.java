package com.demo.backend.controller;

import com.demo.backend.model.dto.TutorialDto;
import com.demo.backend.utils.DataValidation;
import com.demo.backend.service.TutorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/tutorials")
@RequiredArgsConstructor
public class TutorialController {

    private final TutorialService tutorialService;

    @GetMapping
    public ResponseEntity<?> getAllTutorials(){
        return ResponseEntity.ok(tutorialService.getAllTutorials());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTutorialById(@PathVariable long id){
        DataValidation.validateInputParamsOrElseThrowException(id);

        return ResponseEntity.ok(tutorialService.getTutorialById(id));
    }

    @PostMapping
    public ResponseEntity<?> saveTutorial(@RequestBody TutorialDto tutorialDto){
        DataValidation.validateTutorialDtoRequest(tutorialDto);

        return ResponseEntity.ok(tutorialService.saveTutorial(tutorialDto));
    }
}

