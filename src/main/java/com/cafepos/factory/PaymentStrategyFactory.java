package com.cafepos.factory;

import com.cafepos.payment.*;

public final class PaymentStrategyFactory {
    public static PaymentStrategy create(String paymentType, String paymentDetails) {
        if (paymentType == null) return new CashPayment();
        return switch (paymentType.toUpperCase()) {
            case "CASH" -> new CashPayment();
            case "CARD" -> new CardPayment(paymentDetails != null ? paymentDetails : "1234567890123456");
            case "WALLET" -> new WalletPayment(paymentDetails != null ? paymentDetails : "user-wallet-123");
            default -> new CashPayment();
        };
    }
}