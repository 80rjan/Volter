package com.volter.shop.modules.alert.domain.model.enums;

public enum RiskAlertType {
    // Transaction anomalies
    TRANSACTION_ANOMALY,              // Renewal for unusual amount, redeemed for unusual amount
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
