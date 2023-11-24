package com.demo.backend.tutorial.repository;

import com.demo.backend.tutorial.model.Status;
import com.demo.backend.tutorial.model.Tutorial;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialRepository extends CrudRepository<Tutorial, Long> {
    List<Tutorial> findByTitle(String title);

    List<Tutorial> findByStatus(Status status);

    List<Tutorial> findByDescription(String description);

}
