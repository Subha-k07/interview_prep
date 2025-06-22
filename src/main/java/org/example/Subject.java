package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Subject {
    @Id
    private String name; // e.g., DSA, DBMS, OOPS

    @OneToMany(mappedBy = "subject")
    private List<Subtopic> subtopics;

    // Constructors
    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Subtopic> getSubtopics() {
        return subtopics;
    }

    public void setSubtopics(List<Subtopic> subtopics) {
        this.subtopics = subtopics;
    }
}