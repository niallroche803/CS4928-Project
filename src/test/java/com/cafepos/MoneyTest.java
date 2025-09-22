package com.cafepos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.cafepos.common.Money;

public class MoneyTest {
    
    @Test
    void money_creation_from_double() {
        Money m = Money.of(2.50);
        assertEquals("2.50", m.toString());
    }
    
    @Test
    void money_zero() {
        Money zero = Money.zero();
        assertEquals("0.00", zero.toString());
    }
    
    @Test
    void money_addition() {
        Money m1 = Money.of(2.00);
        Money m2 = Money.of(3.00);
        Money result = m1.add(m2);
        assertEquals(Money.of(5.00), result);
    }
    
    @Test
    void money_multiplication() {
        Money m = Money.of(2.50);
        Money result = m.multiply(3);
        assertEquals(Money.of(7.50), result);
    }
    
    @Test
    void money_rounding() {
        Money m = Money.of(2.555); // Should round to 2.56
        assertEquals("2.56", m.toString());
    }
    
    @Test
    void negative_money_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(-1.00));
    }
    
    @Test
    void money_equality() {
        Money m1 = Money.of(2.50);
        Money m2 = Money.of(2.50);
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }
}