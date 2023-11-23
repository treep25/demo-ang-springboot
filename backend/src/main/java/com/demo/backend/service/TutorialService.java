package com.demo.backend.service;

import com.demo.backend.model.SearchingTutorialRequest;
import com.demo.backend.model.Status;
import com.demo.backend.model.Tutorial;
import com.demo.backend.model.dto.TutorialDto;
import com.demo.backend.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TutorialService {

    private final TutorialRepository tutorialRepository;
    @Value("${upload.path}")
    private String uploadPath;

    private final Map<Predicate<SearchingTutorialRequest>, BiFunction<SearchingTutorialRequest, TutorialRepository, List<Tutorial>>> searchingMap = new HashMap<>() {{
        put(request -> verifyRequestParamsNotEmptyAndBlank(request.getTitle()), (request, repository) -> repository.findByTitle(request.getTitle()));
        put(request -> verifyRequestParamsNotEmptyAndBlank(request.getDescription()), (request, repository) -> repository.findByDescription(request.getDescription()));
        put(request -> verifyRequestParamsNotEmptyAndBlank(request.getTutorialStatus()), (request, repository) -> repository.findByStatus(Status.fromTextStatus(request.getTutorialStatus())));
        put(request -> verifyRequestParamsNotEmptyAndBlank(request.getSortingType()), (request, repository)
                -> request.isNaturalOrderSortingType() ?
                getAllTutorials().stream().sorted(Comparator.comparing(Tutorial::getTitle)).toList() :
                getAllTutorials().stream().sorted(Comparator.comparing(Tutorial::getTitle).reversed()).toList());
    }};

    private boolean verifyRequestParamsNotEmptyAndBlank(String request) {
        return request != null && !request.isEmpty() && !request.isBlank();
    }

    public List<Tutorial> getAllTutorials() {
        return StreamSupport.stream(
                tutorialRepository.findAll().spliterator(), false
        ).toList();
    }

    public Tutorial getTutorialById(long id) {
        return tutorialRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public Tutorial saveTutorial(TutorialDto tutorialDto) throws IOException {
        Tutorial preSavedTutorial = Tutorial
                .builder()
                .title(tutorialDto.getTitle())
                .description(tutorialDto.getDescription())
                .status(Status.PENDING)
                .content(tutorialDto.getContent())
                .overview(tutorialDto.getOverview())
                .build();
        if (tutorialDto.getImage() != null && !tutorialDto.getImage().isEmpty()) {
            // TODO: 11/23/2023 refactored this
            String fileName = System.currentTimeMillis() + "_" + tutorialDto.getImage().getOriginalFilename();
            Path filePath = Paths.get(uploadPath, fileName);
            Files.copy(tutorialDto.getImage().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            preSavedTutorial.setImagePath(filePath.toString());
        }

        return tutorialRepository.save(preSavedTutorial);
    }

    public void deleteTutorialById(long id) {
        getTutorialById(id);

        tutorialRepository.deleteById(id);
    }

    public void deleteAllTutorials() {
        tutorialRepository.deleteAll();
    }

    public List<Tutorial> findTutorialByDifferentParams(SearchingTutorialRequest searchingTutorialRequest) {
        AtomicReference<List<Tutorial>> response = new AtomicReference<>(new ArrayList<>());
        searchingMap.forEach((searchingTutorialRequestPredicate, repositoryAction) -> {
            if (searchingTutorialRequestPredicate.test(searchingTutorialRequest)) {
                response.set(repositoryAction.apply(searchingTutorialRequest, tutorialRepository));
            }
        });
        return response.get().isEmpty() ? null : response.get();
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

    public Tutorial updateTutorialById(long id, boolean published) {
        Tutorial tutorialById = getTutorialById(id);
        tutorialById.setStatus(Status.fromBoolean(published));

        return tutorialRepository.save(tutorialById);
    }
}
