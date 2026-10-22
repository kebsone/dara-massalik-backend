
package com.daramassalik.backend.student;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StudentPhotoService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private final Path storageDirectory;

    public StudentPhotoService(
            @Value("${app.uploads.student-photos-dir:uploads/student-photos}")
            String storageDirectory
    ) {
        this.storageDirectory = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Please select a photo"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Photo size must not exceed 5 MB"
            );
        }

        try {
            byte[] bytes = file.getBytes();
            String extension = detectExtension(bytes);

            Files.createDirectories(storageDirectory);

            String filename = UUID.randomUUID() + extension;
            Path destination = storageDirectory.resolve(filename).normalize();

            if (!destination.startsWith(storageDirectory)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid filename"
                );
            }

            Files.write(destination, bytes);
            return filename;

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to save student photo",
                    e
            );
        }
    }

    public StoredPhoto load(String filename) {
        if (filename == null || !filename.matches(
                "[a-fA-F0-9-]{36}\\.(jpg|png|webp)"
        )) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Photo not found"
            );
        }

        try {
            Path photoPath = storageDirectory.resolve(filename).normalize();

            if (!photoPath.startsWith(storageDirectory)
                    || !Files.isRegularFile(photoPath)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Photo not found"
                );
            }

            Resource resource = new UrlResource(photoPath.toUri());

            MediaType mediaType = filename.endsWith(".png")
                    ? MediaType.IMAGE_PNG
                    : filename.endsWith(".webp")
                    ? MediaType.parseMediaType("image/webp")
                    : MediaType.IMAGE_JPEG;

            return new StoredPhoto(resource, mediaType);

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to read student photo",
                    e
            );
        }
    }

    public void delete(String filename) {
        if (filename == null) {
            return;
        }

        if (!filename.matches("[a-fA-F0-9-]{36}\\.(jpg|png|webp)")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid stored photo filename"
            );
        }

        try {
            Path photoPath = storageDirectory.resolve(filename).normalize();

            if (!photoPath.startsWith(storageDirectory)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid photo path"
                );
            }

            Files.deleteIfExists(photoPath);

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to delete student photo",
                    e
            );
        }
    }

    private String detectExtension(byte[] bytes) {
        // JPEG signature
        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return ".jpg";
        }

        // PNG signature
        if (bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47
                && bytes[4] == 0x0D
                && bytes[5] == 0x0A
                && bytes[6] == 0x1A
                && bytes[7] == 0x0A) {
            return ".png";
        }

        // WebP signature: RIFF....WEBP
        if (bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P') {
            return ".webp";
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Unsupported image. Use JPEG, PNG or WebP."
        );
    }

    public record StoredPhoto(Resource resource, MediaType mediaType) {
    }
}
