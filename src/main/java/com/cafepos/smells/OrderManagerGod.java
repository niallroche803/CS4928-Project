package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.*;
import com.cafepos.catalog.Product;
import com.cafepos.pricing.*;

public class OrderManagerGod { //God Class - too many responsibilities

    private static final TaxPolicy TAX_POLICY = new FixedRateTaxPolicy(10);
    public static String LAST_DISCOUNT_CODE = null; //Global/Static state - introduces shared mutable state.


    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) { //Long Method - violates SRP by performing creation, pricing, discounting, tax, payment I/O, and printing.
        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);
        Money unitPrice;
        try {
            var priced = product instanceof com.cafepos.decorator.Priced p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }
        if (qty <= 0) qty = 1;
        Money subtotal = unitPrice.multiply(qty);
        DiscountPolicy discountPolicy = DiscountPolicyFactory.create(discountCode);
        Money discount = discountPolicy.discountOf(subtotal);
        if (discountCode != null) {
            LAST_DISCOUNT_CODE = discountCode; //Global/Static state - mutable global state
        }
        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal())); //Duplicated Logic - Money calculations scattered inline
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero(); //Duplicated Logic - validation logic inline
        Money tax = TAX_POLICY.taxOn(discounted);
        var total = discounted.add(tax);
        if (paymentType != null) {
            if (paymentType.equalsIgnoreCase("CASH")) { //Shotgun Surgery risk - payment logic hardcoded
                System.out.println("[Cash] Customer paid " + total + " EUR");
            } else if (paymentType.equalsIgnoreCase("CARD")) { //Shotgun Surgery risk - payment logic hardcoded
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) { //Shotgun Surgery risk - payment logic hardcoded
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }
        }
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
                receipt.append("Subtotal: ").append(subtotal).append("\n");
        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        receipt.append("Tax (").append(((FixedRateTaxPolicy)TAX_POLICY).getPercent()).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total);
        String out = receipt.toString();
        if (printReceipt) {
            System.out.println(out);
        }
        return out;
    }
}