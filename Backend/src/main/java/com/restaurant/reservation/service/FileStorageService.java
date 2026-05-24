package com.restaurant.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload.dir:uploads/restaurants}")
    private String uploadDir;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    /**
     * Save uploaded image file and return a relative web path (e.g. uploads/restaurants/xyz.png).
     * Never stores an absolute OS path so the frontend can construct a proper URL.
     */
    public String saveRestaurantImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, and GIF are allowed");
        }
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit of " + maxFileSize + " bytes");
        }

        // Create upload directory if it does not exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        file.transferTo(filePath.toFile());

        // Return relative web path — NOT the absolute OS path
        return "uploads/restaurants/" + uniqueFilename;
    }

    /**
     * Delete image file.
     * Handles both legacy absolute paths and new relative web paths.
     */
    public boolean deleteRestaurantImage(String storedPath) {
        if (storedPath == null || storedPath.isEmpty()) {
            return true;
        }
        try {
            Path path;
            Path rawPath = Paths.get(storedPath.replace("/", java.io.File.separator));
            if (rawPath.isAbsolute()) {
                // Legacy record: absolute OS path stored in DB
                path = rawPath;
            } else {
                // New record: relative web path like uploads/restaurants/xyz.png
                // Resolve against the root of the project (parent-parent of uploadDir)
                Path uploadAbsolute = Paths.get(uploadDir).toAbsolutePath();
                // Go up two levels: restaurants -> uploads -> project root
                Path base = uploadAbsolute.getParent().getParent();
                path = base.resolve(rawPath);
            }
            if (Files.exists(path)) {
                Files.delete(path);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
               (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif"));
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
