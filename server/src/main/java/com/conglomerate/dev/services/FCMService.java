package com.conglomerate.dev.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("conglomerate-backend-firebase-adminsdk-h09pe-6c4bc6ee81.json").getInputStream())).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("initialized FCM");
            }
        } catch (IOException e) {
            System.out.println("error initalizing FCM:");
            System.out.println(e);
        }
    }

    public String sendNotification(String deviceToken) throws InterruptedException, ExecutionException {
        Notification notification = Notification.builder()
                .setTitle("Title")
                .setBody("Body")
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(deviceToken)
                .build();

        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }
}
