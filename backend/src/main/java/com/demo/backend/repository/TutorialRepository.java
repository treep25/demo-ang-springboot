package com.demo.backend.repository;

import com.demo.backend.model.Tutorial;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialRepository extends CrudRepository<Tutorial, Long> {
    List<Tutorial> findByTitle(String title);

    List<Tutorial> findByDescription(String description);

}
