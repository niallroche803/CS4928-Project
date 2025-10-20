package com.cafepos.payment;

import com.cafepos.domain.Order;
import com.cafepos.pricing.FixedRateTaxPolicy;

public final class CashPayment implements PaymentStrategy { 
    @Override 
    public void pay(Order order) { 
        System.out.println("[Cash] Customer paid " + order.totalWithTax(new FixedRateTaxPolicy(10)) + " EUR"); 
    } 
}