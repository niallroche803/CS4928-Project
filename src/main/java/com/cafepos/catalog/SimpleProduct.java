package com.cafepos.catalog;

import com.cafepos.common.Money;

import java.util.Objects;

public final class SimpleProduct implements Product {
    private final String id;
    private final String name;
    private final Money basePrice;

    public SimpleProduct (String id, String name, Money basePrice)
    { if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name required");
        }
        if (basePrice == null) {
            throw new IllegalArgumentException("basePrice required");
        }
        
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Money basePrice() {
        return basePrice;
    }

    @Override
    public String toString() { return name + " (€" + basePrice + ")"; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleProduct p)) return false;
        return id.equals(p.id);
    }

    @Override public int hashCode() { return Objects.hash(id); }
}