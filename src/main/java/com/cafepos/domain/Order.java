package com.cafepos.domain;

import java.util.ArrayList;
import java.util.List;

import com.cafepos.common.Money;

public final class Order {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();

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
        if (percent < 0) throw new IllegalArgumentException("tax percent cannot be negative");
        return subtotal().multiply(percent * 0.01);
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }
}