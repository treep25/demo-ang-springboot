package com.demo.backend.utils;

import com.demo.backend.model.Status;
import com.demo.backend.model.dto.TutorialDto;

import java.util.List;
import java.util.Optional;

public class DataValidation {

    public static void validateInputParamsOrElseThrowException(long id) {
        if (id <= 0) {
            throw new RuntimeException("Id should be more then 0");
        }
    }

    public static void validateStatus(String status) {
        if (!Status.iStatusValidFromText(status)) {
            throw new RuntimeException("Invalid parameter tutorialStatus [PENDING, PUBLISHED]");
        }
    }

    public static void preUpdateValidationOfFields(String title, String description, String sortedType, String tutorialStatus) {
        validateOptionalParameter(title, "Invalid parameter title");
        validateOptionalParameter(description, "Invalid parameter description");
        validateOptionalSortingType(sortedType);
        validateOptionalTutorialStatus(tutorialStatus);
    }

    private static void validateOptionalParameter(String parameter, String errorMessage) {
        if (parameter != null && !parameter.trim().isEmpty()) {
            validateParameter(parameter, errorMessage);
        }
    }

    private static void validateOptionalSortingType(String sortedType) {
        if (sortedType != null && !sortedType.trim().isEmpty()) {
            validateSortingType(sortedType);
        }
    }

    private static void validateOptionalTutorialStatus(String tutorialStatus) {
        if (tutorialStatus != null && !tutorialStatus.trim().isEmpty()) {
            validateTutorialStatus(tutorialStatus);
        }
    }

    private static void validateParameter(String parameter, String errorMessage) {
        Optional.ofNullable(parameter)
                .filter(p -> !p.trim().isEmpty())
                .orElseThrow(() -> new RuntimeException(errorMessage));
    }

    private static void validateSortingType(String sortedType) {
        Optional.ofNullable(sortedType)
                .filter(s -> !s.trim().isEmpty())
                .filter(DataValidation::isSortingTypeValid)
                .orElseThrow(() -> new RuntimeException("Invalid parameter sortedType [ASC, DESC]"));
    }

    private static void validateTutorialStatus(String tutorialStatus) {
        Optional.ofNullable(tutorialStatus)
                .filter(s -> !s.trim().isEmpty())
                .filter(Status::iStatusValidFromText)
                .orElseThrow(() -> new RuntimeException("Invalid parameter tutorialStatus [PENDING, PUBLISHED]"));
    }

    private static boolean isSortingTypeValid(String sortedType) {
        return List.of("ASC", "DESC").contains(sortedType.toUpperCase());
    }

    public static void validateTutorialDtoRequest(TutorialDto tutorialDto) {
        if (tutorialDto.getTitle() == null
                || tutorialDto.getTitle().isEmpty()
                || tutorialDto.getTitle().isBlank()
                || tutorialDto.getDescription() == null
                || tutorialDto.getDescription().isEmpty()
                || tutorialDto.getDescription().isBlank()) {

            throw new RuntimeException("Invalid Tutorial");
        }
    }
}
