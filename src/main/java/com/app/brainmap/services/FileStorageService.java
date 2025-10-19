package com.app.brainmap.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(MultipartFile file, String subFolder);
    String storeLocal(MultipartFile file, String subFolder);
}

