package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.payment.*;
import com.cafepos.pricing.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    
    @Test
    void order_totals() {
        var p1 = new SimpleProduct("A", "A", Money.of(2.50));
        var p2 = new SimpleProduct("B", "B", Money.of(3.50));
        var o = new Order(1);
        
        o.addItem(new LineItem(p1, 2));
        o.addItem(new LineItem(p2, 1));
        
        var taxPolicy = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(8.50), o.subtotal());
        assertEquals(Money.of(0.85), o.tax(taxPolicy));
        assertEquals(Money.of(9.35), o.totalWithTax(taxPolicy));
    }
    
    @Test
    void empty_order_totals() {
        Order order = new Order(1);
        var taxPolicy = new FixedRateTaxPolicy(10);
        assertEquals(Money.zero(), order.subtotal());
        assertEquals(Money.zero(), order.tax(taxPolicy));
        assertEquals(Money.zero(), order.totalWithTax(taxPolicy));
    }
    
    @Test
    void single_item_order() {
        var product = new SimpleProduct("P1", "Product 1", Money.of(5.00));
        var order = new Order(1);
        order.addItem(new LineItem(product, 1));
        
        var taxPolicy = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(5.00), order.subtotal());
        assertEquals(Money.of(0.50), order.tax(taxPolicy));
        assertEquals(Money.of(5.50), order.totalWithTax(taxPolicy));
    }
    
    @Test
    void multiple_quantity_line_item() {
        var product = new SimpleProduct("P1", "Product 1", Money.of(2.00));
        var lineItem = new LineItem(product, 3);
        
        assertEquals(Money.of(6.00), lineItem.lineTotal());
    }
    
    @Test
    void invalid_line_item_quantity() {
        var product = new SimpleProduct("P1", "Product 1", Money.of(2.00));
        assertThrows(IllegalArgumentException.class, () -> new LineItem(product, 0));
        assertThrows(IllegalArgumentException.class, () -> new LineItem(product, -1));
    }
    
    @Test
    void payment_strategy_called() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(p, 1));
        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;
        order.pay(fake);
        assertTrue(called[0], "Payment strategy should be called");
    }
    
    @Test
    void order_mark_methods_do_not_throw() {
        var order = new Order(1);
        assertDoesNotThrow(() -> order.markItemAdded());
        assertDoesNotThrow(() -> order.markPaid());
        assertDoesNotThrow(() -> order.markReady());
    }
    
    @Test
    void order_id_is_preserved() {
        var order = new Order(123);
        assertEquals(123, order.id());
    }
}
