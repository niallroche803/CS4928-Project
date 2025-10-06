package com.cafepos.observers;

import com.cafepos.domain.Order;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("itemAdded".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": item added");
        } else if ("paid".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": payment received");
        }
    }
}
