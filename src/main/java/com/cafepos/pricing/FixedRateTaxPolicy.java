package com.cafepos.pricing;

import com.cafepos.common.Money;
import java.math.BigDecimal;

public final class FixedRateTaxPolicy implements TaxPolicy {
    private final int percent;
    public FixedRateTaxPolicy(int percent) {
        if (percent < 0) throw new IllegalArgumentException();
        this.percent = percent;
    }
    @Override public Money taxOn(Money amount) {
        BigDecimal taxAmount = amount.asBigDecimal()
            .multiply(BigDecimal.valueOf(percent))
            .divide(BigDecimal.valueOf(100));
        return Money.of(taxAmount);
    }

    public int getPercent() {
        return percent;
    }
}
