package com.example.fcmnode.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.List;

@Service
public class FCMService {

    @Value("${gcp.project.name}")
    private String PROJECT_NAME;

    @Value("${app.key.filename}")
    private String KEY_FILE_NAME;

    public String getAccessToken() throws IOException {
        InputStream inputStream;
        
        // Verificar si es contenido JSON (comienza con '{') o nombre de archivo
        if (KEY_FILE_NAME.trim().startsWith("{")) {
            // Es contenido JSON directo
            inputStream = new ByteArrayInputStream(KEY_FILE_NAME.getBytes());
        } else {
            // Es nombre de archivo
            ClassPathResource resource = new ClassPathResource(KEY_FILE_NAME);
            inputStream = resource.getInputStream();
        }
        
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshIfExpired();
        AccessToken token = googleCredentials.getAccessToken();
        return token.getTokenValue();
    }

    @Autowired
    private RestTemplate restTemplate;

    public String POSTtoFCM(String json, String FCM_KEY) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; UTF-8");
        headers.set("Authorization", "Bearer " + FCM_KEY);

        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://fcm.googleapis.com/v1/projects/"+PROJECT_NAME+"/messages:send", request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("HTTP Error: " + response.getStatusCode() + ", " + response.getBody());
        }
    }
}
