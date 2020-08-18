package com.conglomerate.dev.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class S3Service {
    /*
    Main functionality largely taken from
    https://blogs.ashrithgn.com/aws-s3-with-spring-boot-uploading-and-downloading-file-to-buckets/
     */

    @Autowired
    AmazonS3 amazonS3Client;

    @Value("${amazonProperties.bucketName}")
    String defaultBucketName;

    @Value("${amazonProperties.endpointUrl}")
    String defaultBaseFolder;

    public void uploadFile(File uploadFile) {
        amazonS3Client.putObject(defaultBucketName, uploadFile.getName(), uploadFile);
    }

    public String uploadFile(String name, byte[] content) throws IOException {
        File directory = new File("tmp/" + name.substring(0, name.lastIndexOf("/")));
        directory.mkdirs();


        File file = new File("tmp/" + name);
        file.canWrite();
        file.canRead();
        FileOutputStream iofs = null;
        try {
            iofs = new FileOutputStream(file);
            iofs.write(content);
            iofs.close();
            System.out.println("Uploading file to " + name);
            amazonS3Client.putObject(new PutObjectRequest(defaultBucketName, name, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            System.out.println(file.delete() ? "Temp file deleted" : "File could not be deleted");

            return amazonS3Client.getUrl(defaultBucketName, name)
                    .toExternalForm();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        file.delete();
        return null;
    }

    public byte[] getFile(String key) {
        S3Object obj = amazonS3Client.getObject(defaultBucketName, defaultBaseFolder + "/" + key);
        S3ObjectInputStream stream = obj.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(stream);
            obj.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteFile(String key) {
        try {
            System.out.println("Deleting file with name " + key);
            amazonS3Client.deleteObject(new DeleteObjectRequest(defaultBucketName, key));
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void logStartup() {
        System.out.println("Initialized S3 Service");
    }

}
