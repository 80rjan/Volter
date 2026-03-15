package com.volter.backend.notification.enums;

public enum NotificationType {
    // Transaction anomalies
    TRANSACTION_ANOMALY,              // Renewal for unusual amount, redemption price mismatch
    PRICE_DEVIATION,                  // Item valued way above/below market rate

    // Customer risk
    HIGH_RISK_CUSTOMER_TRANSACTION,   // Transaction with flagged customer

    // Fraud detection
    DUPLICATE_ITEM_DETECTED,          // Same serial number/description pawned twice

    // Data integrity
    PAWN_MODIFICATION,                // Any change to pawn after creation

    // Staff behavior
    AFTER_HOURS_TRANSACTION,          // Transaction outside business hours

}
