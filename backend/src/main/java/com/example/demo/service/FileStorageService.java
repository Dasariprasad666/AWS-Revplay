package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // This is the folder in the root of your project where files will be saved
    private final String UPLOAD_DIR = "uploads/";

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            // Create the uploads folder if it doesn't exist
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a unique filename so files don't overwrite each other
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // Save the physical file
            Files.copy(file.getInputStream(), filePath);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file. Please try again!", e);
        }
    }

    public void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + fileName));
            } catch (IOException e) {
                System.err.println("Failed to delete physical file: " + fileName);
            }
        }
    }
}