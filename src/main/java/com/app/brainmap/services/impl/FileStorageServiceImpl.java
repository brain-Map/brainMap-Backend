package com.app.brainmap.services.impl;

import com.app.brainmap.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.storage.base-path:uploads}")
    private String basePath;

    @Value("${supabase.project.url}")
    private String supabaseUrl;

    @Value("${supabase.service.role.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final WebClient webClient = WebClient.builder().build();

    @Override
    public String storeLocal(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx > -1) ext = original.substring(idx);
        String newName = UUID.randomUUID() + "_" + Instant.now().toEpochMilli() + ext;
        Path root = Path.of(basePath).toAbsolutePath().normalize();
        Path dir = root.resolve(subFolder).normalize();
        try {
            Files.createDirectories(dir);
            Path dest = dir.resolve(newName);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
//            return dest.toString();
            return basePath + "/" + subFolder + "/" + newName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

@Override
public String store(MultipartFile file, String subFolder) {
    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    String pathInBucket = subFolder + "/" + fileName;

    String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + pathInBucket;

    try {
        webClient.put()
                .uri(uploadUrl)
                .header("Authorization", "Bearer " + supabaseKey)
                .contentType(MediaType.valueOf(file.getContentType()))
                .bodyValue(file.getBytes())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    } catch (IOException e) {
        throw new RuntimeException("Failed to read file bytes", e);
    }

    return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + pathInBucket;
}

}

