package com.example.fcmnode.controller;

import com.example.fcmnode.service.FCMService;
import com.example.fcmnode.service.FitnessNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/fcm")
public class FCMController {

    @Autowired
    private FCMService service;

    @Autowired
    private FitnessNotificationService fitnessNotificationService;

    @PostMapping(value = "messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> send(@RequestBody String data){
        try {
            var token = service.getAccessToken();
            String response = service.POSTtoFCM(data, token);
            return ResponseEntity.status(200).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Enviar consejo fitness diario
    @PostMapping(value = "/fitness-advice", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendFitnessAdvice(){
        try {
            fitnessNotificationService.sendDailyFitnessAdvice();
            return ResponseEntity.status(200).body("Consejo fitness enviado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Recordatorio de foto semanal
    @PostMapping(value = "/weekly-photo-reminder", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendWeeklyPhotoReminder() {
        try {
            fitnessNotificationService.sendWeeklyPhotoReminder();
            return ResponseEntity.status(200).body("Recordatorio semanal enviado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

}