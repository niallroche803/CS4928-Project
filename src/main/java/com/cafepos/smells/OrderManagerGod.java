package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.*;
import com.cafepos.catalog.Product;
import com.cafepos.pricing.*;
import com.cafepos.payment.*;
import com.cafepos.domain.*;

public class OrderManagerGod { //God Class - too many responsibilities

    private static final TaxPolicy TAX_POLICY = new FixedRateTaxPolicy(10);
    public static String LAST_DISCOUNT_CODE = null; //Global/Static state - introduces shared mutable state.


    public static String process(String recipe, int qty, String paymentType, String paymentDetails, String discountCode, boolean printReceipt) { //Long Method - violates SRP by performing creation, pricing, discounting, tax, payment I/O, and printing.
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
            PaymentStrategy paymentStrategy = PaymentStrategyFactory.create(paymentType, paymentDetails);
            Order order = new Order(1);
            order.addItem(new LineItem(product, qty));
            paymentStrategy.pay(order);
        }
        ReceiptPrinter printer = new ReceiptPrinter();
        PricingService.PricingResult pricingResult = new PricingService.PricingResult(subtotal, discount, tax, total);
        String out = printer.format(recipe, qty, pricingResult, ((FixedRateTaxPolicy)TAX_POLICY).getPercent());
        if (printReceipt) {
            printer.print(out);
        }
        return out;
    }
}