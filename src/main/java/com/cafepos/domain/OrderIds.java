package com.cafepos.domain;

import java.util.concurrent.atomic.AtomicLong;

public final class OrderIds {
    private static final AtomicLong SEQ = new AtomicLong(1000);
    private OrderIds() {}
    public static long next() { return SEQ.incrementAndGet(); }
}