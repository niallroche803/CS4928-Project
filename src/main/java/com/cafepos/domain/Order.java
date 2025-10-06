package com.cafepos.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cafepos.common.Money;
import com.cafepos.observers.OrderObserver;
import com.cafepos.observers.OrderPublisher;
import com.cafepos.payment.PaymentStrategy;

public final class Order implements OrderPublisher {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();


    public Order(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    public List<LineItem> items() {
        return new ArrayList<>(items);
    }

    public void addItem(LineItem li) { 
        if (li == null || li.quantity() <= 0)
            throw new IllegalArgumentException("line item required");
        items.add(li);
    }

    public Money subtotal() {
        return items.stream().map(LineItem::lineTotal).reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0) throw new IllegalArgumentException("percent >= 0");
        BigDecimal tax = subtotal().asBigDecimal()
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100));
        return Money.of(tax);
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public void pay(PaymentStrategy strategy) { 
    if (strategy == null) 
        throw new IllegalArgumentException("strategy required"); 
    strategy.pay(this);
    }

    @Override
    public void register(OrderObserver o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void unregister(OrderObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver observer : observers) {
            observer.updated(order, eventType);
        }
    }
    public void markReady() {
        notifyObservers(this, "ready");
    }

    public void markItemAdded() {
        notifyObservers(this, "itemAdded");
    }

    public void markPaid() {
        notifyObservers(this, "paid");
    }
}