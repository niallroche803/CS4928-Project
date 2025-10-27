package com.cafepos.payment;

import java.math.BigDecimal;
import com.cafepos.domain.Order;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.common.Money;

public final class CashPayment implements PaymentStrategy { 
    private final Money cashAmount;
    
    public CashPayment() {
        this.cashAmount = null;
    }
    
    public CashPayment(Money cashAmount) {
        this.cashAmount = cashAmount;
    }
    
    public Money getCashAmount() {
        return cashAmount;
    }
    
    @Override 
    public void pay(Order order) { 
        Money total = order.totalWithTax(new FixedRateTaxPolicy(10));
        if (cashAmount != null) {
            BigDecimal changeAmount = cashAmount.asBigDecimal().subtract(total.asBigDecimal());
            Money change = changeAmount.compareTo(BigDecimal.ZERO) >= 0 ? Money.of(changeAmount) : Money.zero();
            System.out.println("[Cash] Customer paid " + cashAmount + " EUR, total: " + total + " EUR, change: " + change + " EUR");
        } else {
            System.out.println("[Cash] Customer paid " + total + " EUR"); 
        }
    } 
}