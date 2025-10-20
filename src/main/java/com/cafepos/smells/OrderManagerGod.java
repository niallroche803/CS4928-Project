package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.catalog.Product;

public class OrderManagerGod { //God Class - too many responsibilities

    public static int TAX_PERCENT = 10; //Primitive Obsession - TAX_PERCENT as int instead of a dedicated type; Global/Static state - introduces shared mutable state.
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
        Money discount = Money.zero();
        if (discountCode != null) { //Primitive Obsession - using strings for discountCode instead of a dedicated type
            if (discountCode.equalsIgnoreCase("LOYAL5")) { //Shotgun Surgery risk - discount logic hardcoded
                discount = Money.of(subtotal.asBigDecimal().multiply(java.math.BigDecimal.valueOf(5)).divide(java.math.BigDecimal.valueOf(100))); //Duplicated Logic - complex BigDecimal math inline
            } else if (discountCode.equalsIgnoreCase("COUPON1")) { //Shotgun Surgery risk - discount logic hardcoded
                discount = Money.of(1.00);
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            LAST_DISCOUNT_CODE = discountCode; //Global/Static state - mutable global state
        }
        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal())); //Duplicated Logic - Money calculations scattered inline
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero(); //Duplicated Logic - validation logic inline
        var tax = Money.of(discounted.asBigDecimal().multiply(java.math.BigDecimal.valueOf(TAX_PERCENT)).divide(java.math.BigDecimal.valueOf(100))); //Primitive Obsession - magic number for tax; Duplicated Logic - tax calculation inline
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
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total);
        String out = receipt.toString();
        if (printReceipt) {
            System.out.println(out);
        }
        return out;
    }
}