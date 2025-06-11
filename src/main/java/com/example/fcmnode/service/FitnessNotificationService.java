package com.example.fcmnode.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class FitnessNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(FitnessNotificationService.class);

    @Autowired
    private FCMService fcmService;

    @Autowired
    private ObjectMapper objectMapper;

    private final List<String> fitnessAdvices = Arrays.asList(
        "Bebe al menos 8 vasos de agua al día para mantenerte hidratado durante tus entrenamientos",
        "Haz al menos 30 minutos de ejercicio cardiovascular 3 veces por semana",
        "No olvides hacer estiramientos antes y después de entrenar para prevenir lesiones",
        "Incluye proteínas en cada comida para ayudar en la recuperación muscular",
        "Duerme entre 7-9 horas diarias para una mejor recuperación y rendimiento",
        "Varía tu rutina de ejercicios cada 4-6 semanas para evitar el estancamiento",
        "Come una comida balanceada 2-3 horas antes de entrenar para tener energía",
        "Descansa al menos un día a la semana para permitir que tus músculos se recuperen",
        "Mantén una postura correcta durante todos tus ejercicios para maximizar resultados",
        "Establece metas realistas y celebra tus pequeños logros en el camino al fitness",
        "¿Andas recibiendo migajas? ¡Mejor ponte a entrenar!"
    );
    
    private final Random random = new Random();

    // Enviar consejo fitness todos los días a las 3:00 PM
    @Scheduled(cron = "0 0 15 * * *")
    public void sendDailyFitnessAdvice() {
        logger.info("Enviando consejo fitness diario...");
        
        try {
            String randomAdvice = getRandomFitnessAdvice();
            String jsonMessage = createFCMMessage(randomAdvice);
            
            String token = fcmService.getAccessToken();
            String response = fcmService.POSTtoFCM(jsonMessage, token);
            
            logger.info("Consejo fitness enviado exitosamente: {}", randomAdvice);
            logger.info("Respuesta FCM: {}", response);
            
        } catch (Exception e) {
            logger.error("Error al enviar consejo fitness: {}", e.getMessage(), e);
        }
    }

    // Enviar recordatorio de subir foto cada viernes a las 6:00 PM
    @Scheduled(cron = "0 0 18 * * FRI")
    public void sendWeeklyPhotoReminder() {
        logger.info("Enviando recordatorio semanal para subir foto...");
        
        try {
            String reminderMessage = "¡No olvides subir tu foto de progreso semanal!";
            String jsonMessage = createFCMMessage(reminderMessage);
            
            String token = fcmService.getAccessToken();
            String response = fcmService.POSTtoFCM(jsonMessage, token);
            
            logger.info("Recordatorio semanal enviado exitosamente: {}", reminderMessage);
            logger.info("Respuesta FCM: {}", response);
            
        } catch (Exception e) {
            logger.error("Error al enviar recordatorio semanal: {}", e.getMessage(), e);
        }
    }

    private String getRandomFitnessAdvice() {
        int randomIndex = random.nextInt(fitnessAdvices.size());
        return fitnessAdvices.get(randomIndex);
    }

    private String createFCMMessage(String advice) throws IOException {
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> messageContent = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        
        data.put("titulo", "Consejo Fitness");
        data.put("mensaje", advice);
        
        messageContent.put("topic", "noti");
        messageContent.put("data", data);
        
        message.put("message", messageContent);
        
        return objectMapper.writeValueAsString(message);
    }
}
