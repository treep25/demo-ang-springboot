package com.demo.backend.service;

import com.demo.backend.model.Tutorial;
import com.demo.backend.model.dto.TutorialDto;
import com.demo.backend.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TutorialService {

    private final TutorialRepository tutorialRepository;

    public List<Tutorial> getAllTutorials(){
        return StreamSupport.stream(
                tutorialRepository.findAll().spliterator(),false
        ).toList();
    }

    public Tutorial getTutorialById(long id){
        return tutorialRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public Tutorial saveTutorial(TutorialDto tutorialDto) {
        Tutorial preSavedTutorial = Tutorial
                .builder()
                .title(tutorialDto.getTitle())
                .description(tutorialDto.getDescription())
                .build();

        return tutorialRepository.save(preSavedTutorial);
    }

    public void deleteTutorialById(long id) {
        getTutorialById(id);

        tutorialRepository.deleteById(id);
    }

    public void deleteAllTutorials() {
        tutorialRepository.deleteAll();
    }

    public List<Tutorial> findTutorialByTitle(String title) {
        return tutorialRepository.findByTitle(title);
    }

    public Tutorial updateTutorialById(long id, TutorialDto tutorialDto) {
        Tutorial tutorialById = getTutorialById(id);
        if (!tutorialDto.getDescription().isEmpty()) {
            tutorialById.setDescription(tutorialDto.getDescription());
        }
        if (!tutorialDto.getTitle().isEmpty()) {
            tutorialById.setTitle(tutorialDto.getTitle());
        }
        return tutorialRepository.save(tutorialById);
    }
}
