package com._1zko.hacknovahackathon26backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Native SQL trick to shuffle rows and pick 1
    @Query(value = "SELECT * FROM question WHERE mode = :mode ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Question findRandomQuestionByMode(@Param("mode") String mode);
}