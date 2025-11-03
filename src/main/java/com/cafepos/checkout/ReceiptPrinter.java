package com.cafepos.checkout;

import com.cafepos.common.Money;
import com.cafepos.pricing.PricingService;

public final class ReceiptPrinter {
    public String format(String recipe, int qty, PricingService.PricingResult pr, int taxPercent) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(pr.subtotal()).append("\n");
        if (pr.discount().asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(pr.discount()).append("\n");
        }
        receipt.append("Tax (").append(taxPercent).append("%): ").append(pr.tax()).append("\n");
        receipt.append("Total: ").append(pr.total());
        return receipt.toString();
    }
    
    public String formatWithChange(String recipe, int qty, PricingService.PricingResult pr, int taxPercent, Money cashPaid, Money change) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(pr.subtotal()).append("\n");
        if (pr.discount().asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(pr.discount()).append("\n");
        }
        receipt.append("Tax (").append(taxPercent).append("%): ").append(pr.tax()).append("\n");
        receipt.append("Total: ").append(pr.total()).append("\n");
        receipt.append("Cash Paid: ").append(cashPaid).append("\n");
        receipt.append("Change: ").append(change);
        return receipt.toString();
    }

    public void print(String receipt) {
        System.out.println(receipt);
    }
}