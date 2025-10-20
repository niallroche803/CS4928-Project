package com.cafepos;

import com.cafepos.smells.OrderManagerGod;
import com.cafepos.factory.*;
import com.cafepos.pricing.*;
import com.cafepos.payment.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class Week6CharacterizationTests {
    @Test void no_discount_cash_payment() {
        OrderManagerGod manager = new OrderManagerGod(new ProductFactory(), DiscountPolicyFactory.create("NONE"), new FixedRateTaxPolicy(10), new ReceiptPrinter(), PaymentStrategyFactory.create("CASH", null));
        String receipt = manager.process("ESP+SHOT+OAT", 1, "CASH", null, "NONE", false);
        assertTrue(receipt.startsWith("Order (ESP+SHOT+OAT) x1"));
        assertTrue(receipt.contains("Subtotal: 3.80"));
        assertTrue(receipt.contains("Tax (10%): 0.38"));
        assertTrue(receipt.contains("Total: 4.18"));
    }
    @Test void loyalty_discount_card_payment() {
        OrderManagerGod manager = new OrderManagerGod(new ProductFactory(), DiscountPolicyFactory.create("LOYAL5"), new FixedRateTaxPolicy(10), new ReceiptPrinter(), PaymentStrategyFactory.create("CARD", "1234567890123456"));
        String receipt = manager.process("LAT+L", 2, "CARD", "1234567890123456", "LOYAL5", false);
        assertTrue(receipt.contains("Subtotal: 7.80"));
        assertTrue(receipt.contains("Discount: -0.39"));
        assertTrue(receipt.contains("Tax (10%): 0.74"));
        assertTrue(receipt.contains("Total: 8.15"));
    }
    @Test void coupon_fixed_amount_and_qty_clamp() {
        OrderManagerGod manager = new OrderManagerGod(new ProductFactory(), DiscountPolicyFactory.create("COUPON1"), new FixedRateTaxPolicy(10), new ReceiptPrinter(), PaymentStrategyFactory.create("WALLET", "user-wallet-789"));
        String receipt = manager.process("ESP+SHOT", 0, "WALLET", "user-wallet-789", "COUPON1", false);
        assertTrue(receipt.contains("Order (ESP+SHOT) x1"));
        assertTrue(receipt.contains("Subtotal: 3.30"));
        assertTrue(receipt.contains("Discount: -1.00"));
        assertTrue(receipt.contains("Tax (10%): 0.23"));
        assertTrue(receipt.contains("Total: 2.53"));
    }
}