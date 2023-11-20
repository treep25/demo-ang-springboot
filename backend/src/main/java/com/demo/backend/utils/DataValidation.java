package com.demo.backend.utils;

import com.demo.backend.model.dto.TutorialDto;

public class DataValidation {

    public static void validateInputParamsOrElseThrowException(long id){
        if(id <= 0){
            throw new RuntimeException("id should be more then 0");
        }
    }

    public static void validateTutorialDtoRequest(TutorialDto tutorialDto) {
        if(tutorialDto.getTitle() == null
                || tutorialDto.getTitle().isEmpty()
                || tutorialDto.getTitle().isBlank()
                || tutorialDto.getDescription() == null
                || tutorialDto.getDescription().isEmpty()
                || tutorialDto.getDescription().isBlank()){

            throw new RuntimeException("Invalid Tutorial");
        }
    }
}
