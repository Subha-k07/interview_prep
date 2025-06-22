
package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;

@Entity
public class DownloadRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String subtopicName;
    private LocalDateTime timestamp;

    public DownloadRecord() {}

    public DownloadRecord(String username, String subtopicName, LocalDateTime timestamp) {
        this.username = username;
        this.subtopicName = subtopicName;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { return; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSubtopicName() { return subtopicName; }
    public void setSubtopicName(String subtopicName) { this.subtopicName = subtopicName; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}