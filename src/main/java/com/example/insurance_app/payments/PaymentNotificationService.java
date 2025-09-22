package com.example.insurance_app.payments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentNotificationService {
    
    // In a real application, you would inject email/SMS services here
    // For now, we'll just log the notifications
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    // Send notifications for overdue payments
    public void sendOverduePaymentNotifications(List<PaymentDto> overduePayments) {
        log.info("Sending overdue payment notifications for {} payments", overduePayments.size());
        
        for (PaymentDto payment : overduePayments) {
            sendOverduePaymentEmail(payment);
        }
    }
        
    private void sendOverduePaymentEmail(PaymentDto payment) {
        String message = String.format(
                "OVERDUE: Payment Required\n\n" +
                "Dear %s,\n\n" +
                "Your premium payment is now OVERDUE by %d day(s).\n\n" +
                "Payment Details:\n" +
                "- Policy Number: %s\n" +
                "- Amount Due: $%.2f\n" +
                "- Original Due Date: %s\n" +
                "- Late Fee: $%.2f\n" +
                "- Total Amount: $%.2f\n\n" +
                "Please make your payment immediately to avoid policy lapse.\n" +
                "If payment is not received within the grace period, your policy may be cancelled.\n\n" +
                "Best regards,\n" +
                "Insurance Company",
                payment.getUserName(),
                payment.getDaysOverdue(),
                payment.getPolicyNumber(),
                payment.getAmount(),
                payment.getDueDate().format(DATE_FORMATTER),
                payment.getLateFeeAmount() != null ? payment.getLateFeeAmount() : BigDecimal.ZERO,
                payment.getTotalAmount()
        );
        
        // In a real application, this would be sent via email
        log.info("Overdue payment notification for user {}: {}", payment.getUserName(), message);
    }
}
