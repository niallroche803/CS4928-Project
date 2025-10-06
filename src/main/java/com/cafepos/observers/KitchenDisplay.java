package com.cafepos.observers;

import com.cafepos.domain.Order;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("itemAdded".equals(eventType)) {
            if (!order.items().isEmpty()) {
                var lastItem = order.items().get(order.items().size() - 1);
                System.out.println("[Kitchen] Order #" + order.id() + ": " + 
                    lastItem.quantity() + "x " + lastItem.product().name() + " added");
            }
        } else if ("paid".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": Payment received");
        }
    }
}
