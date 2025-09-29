package com.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money implements Comparable<Money> {
    private final BigDecimal amount;

    public static Money of(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money of(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be null or negative");
        }
        return new Money(value);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    private Money(BigDecimal a) {
        if (a == null) throw new IllegalArgumentException("amount required");
        this.amount = a.setScale(2, RoundingMode.HALF_UP);
    }

    public Money add(Money other) { 
        if (other == null)
            throw new IllegalArgumentException("more money required");
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int qty) { 
        if (qty < 0)
            throw new IllegalArgumentException("quantity must be positive");
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty))); 
    }

    public BigDecimal asBigDecimal() { 
        return amount; 
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Money))
            return false;
        Money other = (Money) obj;
        return this.amount.equals(other.amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }
    
    @Override
    public String toString() {
        return String.format("%.2f", amount);
    }
}