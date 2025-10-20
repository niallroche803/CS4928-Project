package com.cafepos.payment;

import com.cafepos.domain.Order;
import com.cafepos.pricing.FixedRateTaxPolicy;

public final class WalletPayment implements PaymentStrategy { 
    private final String walletId; 
    
    public WalletPayment(String walletId) {
        if (walletId == null || walletId.isBlank())
            throw new IllegalArgumentException("walletId required");
        this.walletId = walletId; 
    }

    @Override 
    public void pay(Order order) { 
        System.out.println("[Wallet] Customer paid " + order.totalWithTax(new FixedRateTaxPolicy(10)) + " EUR via wallet " + walletId);
    } 
}