package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadRecordRepository extends JpaRepository<DownloadRecord, Long> {
    List<DownloadRecord> findByUsername(String username);
}