package com.cafepos.domain;

import java.util.ArrayList;
import java.util.List;

import com.cafepos.common.Money;
import com.cafepos.observers.OrderObserver;
import com.cafepos.observers.OrderPublisher;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.pricing.TaxPolicy;
import com.cafepos.pricing.FixedRateTaxPolicy;

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

    public Money tax(TaxPolicy policy) {
        return policy.taxOn(subtotal());
    }

    public Money totalWithTax(TaxPolicy policy) {
        return subtotal().add(tax(policy));
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