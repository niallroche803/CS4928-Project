package com.cafepos.observers;

import com.cafepos.domain.Order;

public final class DeliveryDesk implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("ready".equals(eventType)) {
            System.out.println("[Delivery] Order #" + order.id() + " is ready for delivery");
        }
    }
}
