package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubtopicRepository extends JpaRepository<Subtopic, String> {
    @Query("SELECT s FROM Subtopic s WHERE s.subject.name = ?1")
    List<Subtopic> findBySubjectName(String subjectName);
}