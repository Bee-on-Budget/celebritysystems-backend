package com.celebritysystems.service.impl;

import com.celebritysystems.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${s3.bucket-name}")
    private String bucketName;

    @Value("${s3.path-prefix}")
    private String pathPrefix;

    @Value("${s3.endpoint}")
    private String endpoint;

    @Override
    public String uploadFile(MultipartFile file, String keyPrefix) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        try {
            String fileKey = generateFileKey(keyPrefix, file.getOriginalFilename());
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            
            String fileUrl = buildFileUrl(fileKey);
            log.info("File uploaded successfully to S3: {}", fileUrl);
            return fileUrl;
            
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String fileKey = extractKeyFromUrl(fileUrl);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully from S3: {}", fileUrl);
            
        } catch (AwsServiceException | SdkClientException e) {
            log.error("Failed to delete file from S3: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    @Override
    public String generateFileKey(String keyPrefix, String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String sanitizedFilename = sanitizeFilename(originalFilename);
        
        return String.format("%s/%s/%s/%s-%s", 
                pathPrefix, keyPrefix, timestamp, uniqueId, sanitizedFilename);
    }

    private String buildFileUrl(String fileKey) {
        return String.format("%s/%s/%s", endpoint, bucketName, fileKey);
    }

    private String extractKeyFromUrl(String fileUrl) {
        String baseUrl = String.format("%s/%s/", endpoint, bucketName);
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length());
        }
        throw new IllegalArgumentException("Invalid file URL format: " + fileUrl);
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    @Override
    public Resource downloadFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        try {
            String fileKey = extractKeyFromUrl(fileUrl);
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            byte[] content = s3Object.readAllBytes();
            
            log.info("File downloaded successfully from S3: {}", fileUrl);
            return new ByteArrayResource(content);
            
        } catch (IOException | AwsServiceException | SdkClientException e) {
            log.error("Failed to download file from S3: {}", fileUrl, e);
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }

    @Override
    public byte[] getFileAsBytes(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        try {
            String fileKey = extractKeyFromUrl(fileUrl);
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            byte[] content = s3Object.readAllBytes();
            
            log.info("File retrieved as bytes from S3: {}", fileUrl);
            return content;
            
        } catch (IOException | AwsServiceException | SdkClientException e) {
            log.error("Failed to get file as bytes from S3: {}", fileUrl, e);
            throw new RuntimeException("Failed to get file as bytes from S3", e);
        }
    }

    @Override
    public String generatePresignedUrl(String fileUrl, int expirationMinutes) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        try {
            String fileKey = extractKeyFromUrl(fileUrl);
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            try (S3Presigner presigner = S3Presigner.create()) {
                PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
                String presignedUrl = presignedRequest.url().toString();
                
                log.info("Generated presigned URL for file: {}", fileUrl);
                return presignedUrl;
            }
            
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for file: {}", fileUrl, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}