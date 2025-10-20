package com.cafepos.factory;

import com.cafepos.common.Money;
import com.cafepos.pricing.*;

public final class DiscountPolicyFactory {
    public static DiscountPolicy create(String discountCode) {
        if (discountCode == null) return new NoDiscount();
        return switch (discountCode.toUpperCase()) {
            case "LOYAL5" -> new LoyaltyPercentDiscount(5);
            case "COUPON1" -> new FixedCouponDiscount(Money.of(1.00));
            case "NONE" -> new NoDiscount();
            default -> new NoDiscount();
        };
    }
}