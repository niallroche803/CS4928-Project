package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.pricing.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PricingUnitTests {
    
    // DiscountPolicy test
    @Test void loyalty_discount_5_percent() {
        DiscountPolicy d = new LoyaltyPercentDiscount(5);
        assertEquals(Money.of(0.39), d.discountOf(Money.of(7.80)));
    }
    
    // TaxPolicy test
    @Test void fixed_rate_tax_10_percent() {
        TaxPolicy t = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(0.74), t.taxOn(Money.of(7.41)));
    }
    
    // PricingService test
    @Test void pricing_pipeline() {
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
        var pr = pricing.price(Money.of(7.80));
        assertEquals(Money.of(0.39), pr.discount());
        assertEquals(Money.of(7.41), Money.of(pr.subtotal().asBigDecimal().subtract(pr.discount().asBigDecimal())));
        assertEquals(Money.of(0.74), pr.tax());
        assertEquals(Money.of(8.15), pr.total());
    }
}