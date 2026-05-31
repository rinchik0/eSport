package com.rinchik.esport.service;

import com.rinchik.esport.dto.image.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileStorageService {
    @Value("${app.upload.path}")
    private String uploadPath;

    public ImageUploadResponse saveImage(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadDir.resolve(filename);

        file.transferTo(filePath.toFile());

        ImageUploadResponse response = new ImageUploadResponse();
        response.setImageUrl("/uploads/" + filename);
        return response;
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        Path filePath = Paths.get(fileUrl);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
