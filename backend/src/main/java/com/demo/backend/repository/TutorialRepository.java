package com.demo.backend.repository;

import com.demo.backend.model.Tutorial;
import com.demo.backend.service.TutorialService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorialRepository extends CrudRepository<Tutorial,Long> {
}
