package nl.novi.uploaddownload.service;

import nl.novi.uploaddownload.dto.FileDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocFileDao extends JpaRepository<FileDocument, Long> {
    FileDocument findByFileName(String fileName);
}
