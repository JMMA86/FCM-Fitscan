package com.example.fcmnode.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FCMService {

    private final String PROJECT_NAME;
    private final ObjectMapper objectMapper;
    private final Dotenv dotenv;

    public FCMService() {
        this.objectMapper = new ObjectMapper();
        
        // Intentar cargar .env solo si estamos en desarrollo local
        Dotenv tempDotenv = null;
        try {
            tempDotenv = Dotenv.configure()
                    .directory("./")
                    .filename(".env")
                    .ignoreIfMissing() // No fallar si no existe el archivo
                    .load();
        } catch (Exception e) {
            // Si falla, usar null y obtener variables del sistema
            tempDotenv = null;
        }
        this.dotenv = tempDotenv;
        
        this.PROJECT_NAME = getEnvironmentVariable("GCP_PROJECT_NAME");
    }

    private String getEnvironmentVariable(String key) {
        // Primero intentar obtener del .env (desarrollo local)
        if (dotenv != null) {
            String value = dotenv.get(key);
            if (value != null) {
                return value;
            }
        }
        
        // Si no está en .env o .env no existe, obtener de variables de entorno del sistema
        return System.getenv(key);
    }

    private String buildServiceAccountJson() throws IOException {
        Map<String, String> serviceAccount = new HashMap<>();
        serviceAccount.put("type", getEnvironmentVariable("FCM_TYPE"));
        serviceAccount.put("project_id", getEnvironmentVariable("FCM_PROJECT_ID"));
        serviceAccount.put("private_key_id", getEnvironmentVariable("FCM_PRIVATE_KEY_ID"));
        
        // Procesar la clave privada para reemplazar \\n con saltos de línea reales
        String privateKey = getEnvironmentVariable("FCM_PRIVATE_KEY");
        if (privateKey != null) {
            privateKey = privateKey.replace("\\n", "\n");
        }
        serviceAccount.put("private_key", privateKey);
        
        serviceAccount.put("client_email", getEnvironmentVariable("FCM_CLIENT_EMAIL"));
        serviceAccount.put("client_id", getEnvironmentVariable("FCM_CLIENT_ID"));
        serviceAccount.put("auth_uri", getEnvironmentVariable("FCM_AUTH_URI"));
        serviceAccount.put("token_uri", getEnvironmentVariable("FCM_TOKEN_URI"));
        serviceAccount.put("auth_provider_x509_cert_url", getEnvironmentVariable("FCM_AUTH_PROVIDER_X509_CERT_URL"));
        serviceAccount.put("client_x509_cert_url", getEnvironmentVariable("FCM_CLIENT_X509_CERT_URL"));
        serviceAccount.put("universe_domain", getEnvironmentVariable("FCM_UNIVERSE_DOMAIN"));
        
        return objectMapper.writeValueAsString(serviceAccount);
    }

    public String getAccessToken() throws IOException {
        String serviceAccountJson = buildServiceAccountJson();
        InputStream inputStream = new ByteArrayInputStream(serviceAccountJson.getBytes());
        
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