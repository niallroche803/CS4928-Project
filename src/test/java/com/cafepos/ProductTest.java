package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.catalog.SimpleProduct;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    
    @Test
    void simple_product_creation() {
        SimpleProduct product = new SimpleProduct("P1", "Test Product", Money.of(2.50));
        assertEquals("P1", product.id());
        assertEquals("Test Product", product.name());
        assertEquals(Money.of(2.50), product.basePrice());
    }
    
    @Test
    void product_validation() {
        assertThrows(IllegalArgumentException.class, 
            () -> new SimpleProduct(null, "Test", Money.of(1.00)));
        assertThrows(IllegalArgumentException.class, 
            () -> new SimpleProduct("", "Test", Money.of(1.00)));
        assertThrows(IllegalArgumentException.class, 
            () -> new SimpleProduct("P1", null, Money.of(1.00)));
        assertThrows(IllegalArgumentException.class, 
            () -> new SimpleProduct("P1", "", Money.of(1.00)));
        assertThrows(IllegalArgumentException.class, 
            () -> new SimpleProduct("P1", "Test", null));
    }
}