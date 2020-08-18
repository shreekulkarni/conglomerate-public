package com.conglomerate.dev.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    /*
    Also largely based off of
    https://blogs.ashrithgn.com/aws-s3-with-spring-boot-uploading-and-downloading-file-to-buckets/
     */
    @Value("${amazonProperties.accessKey}")
    String accessKey;
    @Value("${amazonProperties.secretKey}")
    String secretKey;

    @Bean
    public AmazonS3 generateS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();

        System.out.println("Built S3 Client in Configuration");

        return client;
    }
}

