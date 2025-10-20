package com.cafepos.payment;
import com.cafepos.domain.Order;
import com.cafepos.pricing.FixedRateTaxPolicy;

public final class CardPayment implements PaymentStrategy { 
    private final String cardNumber; 
    
    public CardPayment(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            throw new IllegalArgumentException("Card number must be at least 4 digits long");
        }
        this.cardNumber = cardNumber; 
    }

    @Override 
    public void pay(Order order) {
        String maskedCard = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        System.out.println("[Card] Customer paid " + order.totalWithTax(new FixedRateTaxPolicy(10)) + " EUR with card " + maskedCard);
    } 
}