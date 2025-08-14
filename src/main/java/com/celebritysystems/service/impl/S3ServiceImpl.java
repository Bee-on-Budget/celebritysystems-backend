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
import java.util.Arrays;
import java.util.List;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${s3.bucket-name}")
    private String bucketName;

    @Value("${s3.path-prefix}")
    private String pathPrefix;

    @Value("${s3.endpoint}")
    private String endpoint;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp",
        "application/pdf", "text/plain", "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public String uploadFile(MultipartFile file, String keyPrefix) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        validateFile(file);

        try {
            String fileKey = generateFileKey(keyPrefix, file.getOriginalFilename());
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            
            String fileUrl = buildFileUrl(fileKey);
            log.info("File uploaded successfully to S3: {}", fileUrl);
            return fileUrl;
            
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        } catch (AwsServiceException e) {
            log.error("AWS service error while uploading file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("AWS service error during file upload", e);
        } catch (SdkClientException e) {
            log.error("SDK client error while uploading file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("SDK client error during file upload", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + MAX_FILE_SIZE + " bytes");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
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
            
        } catch (AwsServiceException e) {
            log.error("AWS service error while deleting file: {}", fileUrl, e);
            throw new RuntimeException("AWS service error during file deletion", e);
        } catch (SdkClientException e) {
            log.error("SDK client error while deleting file: {}", fileUrl, e);
            throw new RuntimeException("SDK client error during file deletion", e);
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
        String baseUrl = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return String.format("%s/%s/%s", baseUrl, bucketName, fileKey);
    }

    private String extractKeyFromUrl(String fileUrl) {
        String baseUrl = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        String expectedPrefix = String.format("%s/%s/", baseUrl, bucketName);
        
        if (fileUrl.startsWith(expectedPrefix)) {
            return fileUrl.substring(expectedPrefix.length());
        }
        throw new IllegalArgumentException("Invalid file URL format: " + fileUrl);
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_").toLowerCase();
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

            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                byte[] content = s3Object.readAllBytes();
                log.info("File downloaded successfully from S3: {}", fileUrl);
                return new ByteArrayResource(content);
            }
            
        } catch (IOException e) {
            log.error("IO error while downloading file from S3: {}", fileUrl, e);
            throw new RuntimeException("IO error during file download", e);
        } catch (AwsServiceException e) {
            log.error("AWS service error while downloading file: {}", fileUrl, e);
            throw new RuntimeException("AWS service error during file download", e);
        } catch (SdkClientException e) {
            log.error("SDK client error while downloading file: {}", fileUrl, e);
            throw new RuntimeException("SDK client error during file download", e);
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

            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                byte[] content = s3Object.readAllBytes();
                log.info("File retrieved as bytes from S3: {}", fileUrl);
                return content;
            }
            
        } catch (IOException e) {
            log.error("IO error while getting file as bytes from S3: {}", fileUrl, e);
            throw new RuntimeException("IO error during file retrieval", e);
        } catch (AwsServiceException e) {
            log.error("AWS service error while getting file as bytes: {}", fileUrl, e);
            throw new RuntimeException("AWS service error during file retrieval", e);
        } catch (SdkClientException e) {
            log.error("SDK client error while getting file as bytes: {}", fileUrl, e);
            throw new RuntimeException("SDK client error during file retrieval", e);
        }
    }

    @Override
    public String generatePresignedUrl(String fileUrl, int expirationMinutes) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        if (expirationMinutes <= 0 || expirationMinutes > 10080) { // Max 7 days
            throw new IllegalArgumentException("Expiration minutes must be between 1 and 10080 (7 days)");
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

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();
            
            log.info("Generated presigned URL for file: {}", fileUrl);
            return presignedUrl;
            
        } catch (AwsServiceException e) {
            log.error("AWS service error while generating presigned URL: {}", fileUrl, e);
            throw new RuntimeException("AWS service error during presigned URL generation", e);
        } catch (SdkClientException e) {
            log.error("SDK client error while generating presigned URL: {}", fileUrl, e);
            throw new RuntimeException("SDK client error during presigned URL generation", e);
        } catch (Exception e) {
            log.error("Unexpected error while generating presigned URL: {}", fileUrl, e);
            throw new RuntimeException("Unexpected error during presigned URL generation", e);
        }
    }
}
