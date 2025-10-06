package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.observers.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ObserverImplementationsTest {
    
    @Test
    void kitchen_display_handles_item_added() {
        var product = new SimpleProduct("P1", "Espresso", Money.of(2.50));
        var order = new Order(1);
        order.addItem(new LineItem(product, 2));
        
        KitchenDisplay kitchen = new KitchenDisplay();
        assertDoesNotThrow(() -> kitchen.updated(order, "itemAdded"));
    }
    
    @Test
    void kitchen_display_handles_paid() {
        var order = new Order(1);
        KitchenDisplay kitchen = new KitchenDisplay();
        assertDoesNotThrow(() -> kitchen.updated(order, "paid"));
    }
    
    @Test
    void kitchen_display_ignores_other_events() {
        var order = new Order(1);
        KitchenDisplay kitchen = new KitchenDisplay();
        assertDoesNotThrow(() -> kitchen.updated(order, "ready"));
        assertDoesNotThrow(() -> kitchen.updated(order, "unknown"));
    }
    
    @Test
    void delivery_desk_handles_ready() {
        var order = new Order(1);
        DeliveryDesk delivery = new DeliveryDesk();
        assertDoesNotThrow(() -> delivery.updated(order, "ready"));
    }
    
    @Test
    void delivery_desk_ignores_other_events() {
        var order = new Order(1);
        DeliveryDesk delivery = new DeliveryDesk();
        assertDoesNotThrow(() -> delivery.updated(order, "itemAdded"));
        assertDoesNotThrow(() -> delivery.updated(order, "paid"));
    }
    
    @Test
    void customer_notifier_handles_all_events() {
        var order = new Order(1);
        CustomerNotifier notifier = new CustomerNotifier();
        assertDoesNotThrow(() -> notifier.updated(order, "itemAdded"));
        assertDoesNotThrow(() -> notifier.updated(order, "paid"));
        assertDoesNotThrow(() -> notifier.updated(order, "ready"));
        assertDoesNotThrow(() -> notifier.updated(order, "unknown"));
    }
}