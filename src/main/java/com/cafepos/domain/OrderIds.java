package com.cafepos.domain;

public final class OrderIds {
    private static long counter = 1;

    public static long next() {
        return counter++;
    }
}