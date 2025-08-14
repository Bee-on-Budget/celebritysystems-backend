package com.celebritysystems.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadFile(MultipartFile file, String keyPrefix);
    void deleteFile(String fileUrl);
    String generateFileKey(String keyPrefix, String originalFilename);
    Resource downloadFile(String fileUrl);
    byte[] getFileAsBytes(String fileUrl);
    String generatePresignedUrl(String fileUrl, int expirationMinutes);
}
