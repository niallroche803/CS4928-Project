package com.cafepos;

import com.cafepos.domain.*;
import com.cafepos.observers.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderObserverTest {
    
    @Test
    void order_observer_registration() {
        Order order = new Order(1);
        TestObserver observer = new TestObserver();
        
        order.register(observer);
        order.markItemAdded();
        
        assertTrue(observer.wasNotified);
        assertEquals("itemAdded", observer.lastEventType);
    }
    
    @Test
    void order_observer_unregistration() {
        Order order = new Order(1);
        TestObserver observer = new TestObserver();
        
        order.register(observer);
        order.unregister(observer);
        order.markItemAdded();
        
        assertFalse(observer.wasNotified);
    }
    
    @Test
    void order_multiple_observers() {
        Order order = new Order(1);
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();
        
        order.register(observer1);
        order.register(observer2);
        order.markPaid();
        
        assertTrue(observer1.wasNotified);
        assertTrue(observer2.wasNotified);
        assertEquals("paid", observer1.lastEventType);
        assertEquals("paid", observer2.lastEventType);
    }
    
    @Test
    void order_mark_ready_notifies_observers() {
        Order order = new Order(1);
        TestObserver observer = new TestObserver();
        
        order.register(observer);
        order.markReady();
        
        assertTrue(observer.wasNotified);
        assertEquals("ready", observer.lastEventType);
    }
    
    @Test
    void order_null_observer_registration() {
        Order order = new Order(1);
        assertDoesNotThrow(() -> order.register(null));
    }
    
    @Test
    void order_duplicate_observer_registration() {
        Order order = new Order(1);
        TestObserver observer = new TestObserver();
        
        order.register(observer);
        order.register(observer); // Should not add duplicate
        order.markItemAdded();
        
        assertEquals(1, observer.notificationCount);
    }
    
    private static class TestObserver implements OrderObserver {
        boolean wasNotified = false;
        String lastEventType = null;
        int notificationCount = 0;
        
        @Override
        public void updated(Order order, String eventType) {
            wasNotified = true;
            lastEventType = eventType;
            notificationCount++;
        }
    }
}