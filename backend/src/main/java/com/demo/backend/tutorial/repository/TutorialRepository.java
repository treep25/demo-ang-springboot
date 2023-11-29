package com.demo.backend.tutorial.repository;

import com.demo.backend.tutorial.model.Status;
import com.demo.backend.tutorial.model.Tutorial;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialRepository extends CrudRepository<Tutorial, Long> {
    @Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :titleExpected, '%'))")
    List<Tutorial> findByTitle(@Param("titleExpected") String titleExpected);

    List<Tutorial> findByStatus(Status status);

    @Query("SELECT t FROM Tutorial t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :descriptionExpected, '%'))")
    List<Tutorial> findByDescription(@Param("descriptionExpected") String descriptionExpected);
}
