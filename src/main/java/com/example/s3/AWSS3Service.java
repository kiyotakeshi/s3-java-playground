package com.example.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AWSS3Service {

    static final String BUCKET_PREFIX = "java-s3-playground-bucket-";

    private final AmazonS3 s3Client;

    public AWSS3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public Bucket createBucket(LocalDateTime localDateTime) {
        final String yyyyMMddHHmm = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
        final String bucketName = BUCKET_PREFIX + yyyyMMddHHmm;

        if (s3Client.doesBucketExistV2(bucketName)) {
            throw new RuntimeException("try again with a different bucket name");
        }

        return s3Client.createBucket(bucketName);
    }

    // TODO: put objects
}
