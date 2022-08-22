package com.example.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class AWSS3ServiceTests {

    private static final AWSCredentials CREDENTIALS = new BasicAWSCredentials(
                "test",
                "test"
        );
    private static AmazonS3 s3Client;

    private static AWSS3Service sut;

    @BeforeAll
    static void beforeAll() {
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(CREDENTIALS))
                .withEndpointConfiguration(
                        // @see https://github.com/localstack/localstack/issues/2631#issuecomment-813473059
                        new AwsClientBuilder.EndpointConfiguration("http://s3.localhost.localstack.cloud:4566", "ap-northeast-1")
                )
                .build();

        sut = new AWSS3Service(s3Client);
    }

    // TODO: use test containers
    @Test
    void createBucket() {
        LocalDateTime now = LocalDateTime.now();
        sut.createBucket(now);

        long newBucketCount = s3Client.listBuckets()
                .stream()
                .filter(bucket -> bucket.getName().equals(AWSS3Service.BUCKET_PREFIX + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"))))
                .count();

        assertThat(newBucketCount).isEqualTo(1);

        // create same name bucket twice
        assertThatThrownBy(() -> sut.createBucket(now))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("try again with a different bucket name");
    }
}