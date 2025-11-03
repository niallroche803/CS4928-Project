package com.cafepos.smells;

import com.cafepos.checkout.ReceiptPrinter;
import com.cafepos.common.Money;
import com.cafepos.factory.*;
import com.cafepos.catalog.Product;
import com.cafepos.pricing.*;
import com.cafepos.payment.*;
import com.cafepos.domain.*;

public class OrderManagerGod {
    private final ProductFactory factory;
    private final DiscountPolicy discountPolicy;
    private final TaxPolicy taxPolicy;
    private final ReceiptPrinter printer;
    private final PaymentStrategy paymentStrategy;

    public OrderManagerGod(ProductFactory factory, DiscountPolicy discountPolicy, TaxPolicy taxPolicy, ReceiptPrinter printer, PaymentStrategy paymentStrategy) {
        this.factory = factory;
        this.discountPolicy = discountPolicy;
        this.taxPolicy = taxPolicy;
        this.printer = printer;
        this.paymentStrategy = paymentStrategy;
    }

    public String process(String recipe, int qty, String paymentType, String paymentDetails, String discountCode, boolean printReceipt) {
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
        Money discount = discountPolicy.discountOf(subtotal);
        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();
        Money tax = taxPolicy.taxOn(discounted);
        var total = discounted.add(tax);
        if (paymentType != null) {
            Order order = new Order(1);
            order.addItem(new LineItem(product, qty));
            paymentStrategy.pay(order);
        }
        PricingService.PricingResult pricingResult = new PricingService.PricingResult(subtotal, discount, tax, total);
        String out = printer.format(recipe, qty, pricingResult, ((FixedRateTaxPolicy)taxPolicy).getPercent());
        if (printReceipt) {
            printer.print(out);
        }
        return out;
    }
}