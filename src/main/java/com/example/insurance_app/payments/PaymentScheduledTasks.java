package com.example.insurance_app.payments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduledTasks {
    
    private final PaymentService paymentService;
    private final PaymentNotificationService paymentNotificationService;
    
    /**
     * Daily task to check and process overdue payments
     * Runs every day at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void processOverduePayments() {
        log.info("Starting daily overdue payment processing at {}", LocalDateTime.now());
        
        try {
            List<PaymentDto> overduePayments = paymentService.checkOverduePayments();
            log.info("Processed {} overdue payments", overduePayments.size());
            
            // Send notifications for overdue payments
            if (!overduePayments.isEmpty()) {
                paymentNotificationService.sendOverduePaymentNotifications(overduePayments);
            }
            
        } catch (Exception e) {
            log.error("Error processing overdue payments: {}", e.getMessage(), e);
        }
        
        log.info("Completed daily overdue payment processing");
    }
}
