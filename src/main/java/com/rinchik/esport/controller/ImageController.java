package com.rinchik.esport.controller;

import com.rinchik.esport.dto.image.ImageUploadResponse;
import com.rinchik.esport.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uploads")
public class ImageController {
    private final FileStorageService fileStorageService;

    @PostMapping("/new")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        try {
            return ResponseEntity.status(HttpStatus.OK).body(fileStorageService.saveImage(file));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{imageUrl}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageUrl) {
        try {
            Path filePath = Paths.get("uploads", imageUrl);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable())
                return ResponseEntity.status(HttpStatus.OK).body(resource);
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{imageUrl}")
    public ResponseEntity<Void> deleteImage(@PathVariable String imageUrl) {
        try {
            fileStorageService.deleteFile("uploads/" + imageUrl);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
