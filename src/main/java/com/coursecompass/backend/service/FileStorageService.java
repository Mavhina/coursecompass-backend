package com.coursecompass.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path baseDir;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public StoredFile store(MultipartFile file, Long applicationId) {
        try {
            Files.createDirectories(baseDir);

            String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safeOriginal = original.replaceAll("[^a-zA-Z0-9._-]", "_");

            String ext = "";
            int dot = safeOriginal.lastIndexOf('.');
            if (dot >= 0) ext = safeOriginal.substring(dot);

            String unique = UUID.randomUUID().toString().replace("-", "") + ext;

            Path appDir = baseDir.resolve("fee-fund").resolve(String.valueOf(applicationId));
            Files.createDirectories(appDir);

            Path target = appDir.resolve(unique);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return new StoredFile(
                    safeOriginal,
                    target.toString(),  // storageKey
                    null                // publicUrl (optional)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public record StoredFile(String originalFileName, String storageKey, String publicUrl) {}
}
