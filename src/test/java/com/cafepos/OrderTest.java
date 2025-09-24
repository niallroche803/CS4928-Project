package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.payment.PaymentStrategy;
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
        
        assertEquals(Money.of(8.50), o.subtotal());
        assertEquals(Money.of(0.85), o.taxAtPercent(10));
        assertEquals(Money.of(9.35), o.totalWithTax(10));
    }
    
    @Test
    void empty_order_totals() {
        Order order = new Order(1);
        assertEquals(Money.zero(), order.subtotal());
        assertEquals(Money.zero(), order.taxAtPercent(10));
        assertEquals(Money.zero(), order.totalWithTax(10));
    }
    
    @Test
    void single_item_order() {
        var product = new SimpleProduct("P1", "Product 1", Money.of(5.00));
        var order = new Order(1);
        order.addItem(new LineItem(product, 1));
        
        assertEquals(Money.of(5.00), order.subtotal());
        assertEquals(Money.of(0.50), order.taxAtPercent(10));
        assertEquals(Money.of(5.50), order.totalWithTax(10));
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
}
