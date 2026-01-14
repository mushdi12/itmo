package com.example.lab1.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.util.logging.Logger;

@ApplicationScoped
public class MinIOService {

    private static final Logger logger = Logger.getLogger(MinIOService.class.getName());
    
    private static final String BUCKET_NAME = "import-files";
    private static final String ENDPOINT = System.getProperty("minio.endpoint", "http://localhost:9000");
    private static final String ACCESS_KEY = System.getProperty("minio.accessKey", "minioadmin");
    private static final String SECRET_KEY = System.getProperty("minio.secretKey", "minioadmin");
    
    private MinioClient minioClient;
    private boolean initialized = false;

    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(ENDPOINT)
                    .credentials(ACCESS_KEY, SECRET_KEY)
                    .build();
            
            // Проверяем существование bucket, если нет - создаем
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(BUCKET_NAME)
                    .build());
            
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(BUCKET_NAME)
                        .build());
                logger.info("Bucket " + BUCKET_NAME + " created successfully");
            }
            
            initialized = true;
            logger.info("MinIO service initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize MinIO service: " + e.getMessage());
            initialized = false;
        }
    }

    @PreDestroy
    public void cleanup() {
        // MinioClient не требует явного закрытия
    }

    public String uploadFile(InputStream inputStream, String fileName, String contentType, long size) 
            throws MinioException {
        if (!initialized) {
            throw new IllegalStateException("MinIO service is not initialized");
        }

        try {
            String objectName = generateObjectName(fileName);
            
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
            
            logger.info("File uploaded successfully: " + objectName);
            return objectName;
        } catch (Exception e) {
            logger.severe("Failed to upload file to MinIO: " + e.getMessage());
            throw new MinioException("Failed to upload file: " + e.getMessage());
        }
    }

    public InputStream downloadFile(String objectName) throws MinioException {
        if (!initialized) {
            throw new IllegalStateException("MinIO service is not initialized");
        }

        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            logger.severe("Failed to download file from MinIO: " + e.getMessage());
            throw new MinioException("Failed to download file: " + e.getMessage());
        }
    }

    public void deleteFile(String objectName) throws MinioException {
        if (!initialized) {
            throw new IllegalStateException("MinIO service is not initialized");
        }

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .build());
            logger.info("File deleted successfully: " + objectName);
        } catch (Exception e) {
            logger.severe("Failed to delete file from MinIO: " + e.getMessage());
            throw new MinioException("Failed to delete file: " + e.getMessage());
        }
    }

    public String getFileUrl(String objectName) {
        return ENDPOINT + "/" + BUCKET_NAME + "/" + objectName;
    }

    public boolean isAvailable() {
        return initialized;
    }

    private String generateObjectName(String fileName) {
        return System.currentTimeMillis() + "_" + fileName;
    }
}

