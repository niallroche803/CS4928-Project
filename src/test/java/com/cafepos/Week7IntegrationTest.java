package com.cafepos;

import com.cafepos.checkout.CheckoutService;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.*;
import com.cafepos.pricing.*;
import com.cafepos.domain.*;
import com.cafepos.observers.*;
import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Week7IntegrationTest {
    
    @Test
    void full_system_integration_test() {
        // Week 2: Money & Orders
        var factory = new ProductFactory();
        var product = factory.create("ESP+SHOT+OAT");
        var order = new Order(1);
        order.addItem(new LineItem(product, 2));
        
        // Week 3: Payment Strategies
        var cardPayment = new CardPayment("1234567890123456");
        var cashPayment = new CashPayment(Money.of(10.00));
        var walletPayment = new WalletPayment("wallet-123");
        
        // Week 4: Observer Pattern
        var kitchen = new KitchenDisplay();
        var delivery = new DeliveryDesk();
        var customer = new CustomerNotifier();
        
        order.register(kitchen);
        order.register(delivery);
        order.register(customer);
        
        // Week 5: Decorator & Factory
        assertEquals("Espresso + Extra Shot + Oat Milk", product.name());
        
        // Week 6: Refactored Pricing
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(factory, pricing, printer, 10);
        
        String receipt = checkout.checkout("ESP+SHOT+OAT", 2, cardPayment);
        
        assertTrue(receipt.contains("Order (ESP+SHOT+OAT) x2"));
        assertTrue(receipt.contains("Subtotal: 7.60"));
        assertTrue(receipt.contains("Discount: -0.38"));
        assertTrue(receipt.contains("Tax (10%): 0.72"));
        assertTrue(receipt.contains("Total: 7.94"));
        
        // Test observer notifications
        assertDoesNotThrow(() -> {
            order.markItemAdded();
            order.markPaid();
            order.markReady();
        });
    }
    
    @Test
    void all_payment_strategies_work() {
        var order = new Order(1);
        var product = new ProductFactory().create("LAT");
        order.addItem(new LineItem(product, 1));
        
        assertDoesNotThrow(() -> new CardPayment("1234567890123456").pay(order));
        assertDoesNotThrow(() -> new CashPayment(Money.of(5.00)).pay(order));
        assertDoesNotThrow(() -> new WalletPayment("wallet-123").pay(order));
    }
    
    @Test
    void all_decorators_work_with_factory() {
        var factory = new ProductFactory();
        
        assertDoesNotThrow(() -> factory.create("ESP"));
        assertDoesNotThrow(() -> factory.create("ESP+SHOT"));
        assertDoesNotThrow(() -> factory.create("ESP+SHOT+OAT"));
        assertDoesNotThrow(() -> factory.create("ESP+SHOT+OAT+SYP"));
        assertDoesNotThrow(() -> factory.create("LAT+L"));
        assertDoesNotThrow(() -> factory.create("CAP+L+SHOT+OAT+SYP"));
    }
}