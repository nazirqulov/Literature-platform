package uz.literature.platform.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.literature.platform.payload.response.PendingRegistration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by: Barkamol
 * DateTime: 2/5/2026 10:48 AM
 */
@Service
@Slf4j
public class PendingRegistrationService {

    private final Map<String, PendingRegistration> pendingRegistrations = new ConcurrentHashMap<>();

    public void savePendingRegistration(String email, PendingRegistration registration) {
        pendingRegistrations.put(email, registration);
        log.info("Pending registration saved for email: {}", email);
    }

    public PendingRegistration getPendingRegistration(String email) {
        return pendingRegistrations.get(email);
    }

    public void removePendingRegistration(String email) {
        pendingRegistrations.remove(email);
        log.info("Pending registration removed for email: {}", email);
    }

    public boolean hasPendingRegistration(String email) {
        return pendingRegistrations.containsKey(email);
    }

//    // Har 1 soatda muddati o'tgan registratsiyalarni o'chirish
//    @Scheduled(fixedRate = 3600000) // 1 soat
//    public void cleanupExpiredRegistrations() {
//        LocalDateTime now = LocalDateTime.now();
//        pendingRegistrations.entrySet().removeIf(entry -> {
//            if (entry.getValue().getExpiryTime().isBefore(now)) {
//                log.info("Expired pending registration removed: {}", entry.getKey());
//                return true;
//            }
//            return false;
//        });
//    }
}
