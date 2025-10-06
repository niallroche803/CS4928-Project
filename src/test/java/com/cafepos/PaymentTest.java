package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.payment.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    
    @Test
    void cash_payment_processes_order() {
        var product = new SimpleProduct("P1", "Test Product", Money.of(5.00));
        var order = new Order(1);
        order.addItem(new LineItem(product, 1));
        
        CashPayment payment = new CashPayment();
        assertDoesNotThrow(() -> payment.pay(order));
    }
    
    @Test
    void order_pay_null_strategy_throws_exception() {
        var order = new Order(1);
        assertThrows(IllegalArgumentException.class, () -> order.pay(null));
    }
    
    @Test
    void payment_strategy_receives_correct_order() {
        var product = new SimpleProduct("P1", "Test Product", Money.of(5.00));
        var order = new Order(42);
        order.addItem(new LineItem(product, 1));
        
        final Order[] receivedOrder = {null};
        PaymentStrategy testStrategy = o -> receivedOrder[0] = o;
        
        order.pay(testStrategy);
        
        assertSame(order, receivedOrder[0]);
    }
}