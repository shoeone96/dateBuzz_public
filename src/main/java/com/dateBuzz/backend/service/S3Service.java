package com.dateBuzz.backend.service;

import com.dateBuzz.backend.exception.DateBuzzException;
import com.dateBuzz.backend.exception.ErrorCode;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    public String uploadFileByteArray(byte[] fileData, String fileName, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);

            // ACL 설정이 필요하지 않으므로 PutObjectRequest 생성 시에 ACL 설정 제거
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));

            // 리소스 해제를 위해 try-with-resources 사용
            try (inputStream) {
                // 파일 업로드 후 URL 생성
                String objectUrl = amazonS3.getUrl(bucket, fileName).toString();
                return objectUrl;
            }
        } catch (AmazonServiceException | IOException e) {
            // 예외 처리를 개선하여 적절한 조치를 취하도록 함
            throw new DateBuzzException(ErrorCode.S3_UPLOAD_PROBLEM);
        }
    }
    public String uploadProfileImage(MultipartFile profileImg) {
        try {
            // 고유한 파일 이름 생성
            String fileName = UUID.randomUUID().toString() + "_" + profileImg.getOriginalFilename();

            // S3에 파일 업로드
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(profileImg.getSize());
            amazonS3.putObject(bucket, fileName, profileImg.getInputStream(), metadata);

            // 업로드된 파일의 URL 생성
            return amazonS3.getUrl(bucket, fileName).toString();

        } catch (IOException e) {
            // 업로드 실패 처리
            throw new DateBuzzException(ErrorCode.S3_UPLOAD_PROBLEM);
        }
    }
}
