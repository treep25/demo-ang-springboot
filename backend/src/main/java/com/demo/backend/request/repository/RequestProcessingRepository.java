package com.demo.backend.request.repository;

import com.demo.backend.request.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestProcessingRepository extends JpaRepository<Request, Long> {
    @Modifying
    @Query("DELETE FROM Request r WHERE r.status = 'CLOSED'")
    void deleteAllByStatusIsClosed();

}
