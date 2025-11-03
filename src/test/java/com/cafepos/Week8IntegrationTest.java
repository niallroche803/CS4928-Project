package com.cafepos;

import com.cafepos.command.*;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.payment.CashPayment;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import com.cafepos.pricing.FixedRateTaxPolicy;
import vendor.legacy.LegacyThermalPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Week8IntegrationTest {
    private OrderService service;
    private Order order;
    private PosRemote remote;
    private Printer printer;

    @BeforeEach
    void setUp() {
        order = new Order(OrderIds.next());
        service = new OrderService(order);
        remote = new PosRemote(3);
        printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());
    }

    @Test
    void testCompleteOrderWorkflow() {
        // Setup commands
        AddItemCommand addEspresso = new AddItemCommand(service, "ESP", 2);
        AddItemCommand addLatte = new AddItemCommand(service, "LAT", 1);
        PayOrderCommand payOrder = new PayOrderCommand(service, new CashPayment(), 10);

        // Bind to remote slots
        remote.setSlot(0, addEspresso);
        remote.setSlot(1, addLatte);
        remote.setSlot(2, payOrder);

        // Execute workflow
        remote.press(0); // Add 2 espressos
        remote.press(1); // Add 1 latte
        remote.press(2); // Pay order

        assertEquals(2, order.items().size());
        assertTrue(order.totalWithTax(new FixedRateTaxPolicy(10)).compareTo(com.cafepos.common.Money.zero()) > 0);
    }

    @Test
    void testOrderWithUndoAndPrint() {
        AddItemCommand addItem = new AddItemCommand(service, "CAP", 1);
        remote.setSlot(0, addItem);

        // Add item then undo
        remote.press(0);
        assertEquals(1, order.items().size());
        
        remote.undo();
        assertEquals(0, order.items().size());

        // Add again and print receipt
        remote.press(0);
        String receipt = "Order Receipt\nCappuccino x1\nTotal: " + service.totalWithTax(10);
        
        assertDoesNotThrow(() -> printer.printReceipt(receipt));
    }

    @Test
    void testMacroCommandWithPrinting() {
        AddItemCommand add1 = new AddItemCommand(service, "ESP", 1);
        AddItemCommand add2 = new AddItemCommand(service, "LAT", 1);
        
        MacroCommand orderMacro = new MacroCommand(add1, add2);
        remote.setSlot(0, orderMacro);
        
        remote.press(0);
        assertEquals(2, order.items().size());
        
        // Print receipt for the order
        String receipt = "Macro Order Receipt\n" + 
                        order.items().size() + " items\n" +
                        "Total: " + service.totalWithTax(10);
        
        assertDoesNotThrow(() -> printer.printReceipt(receipt));
    }
}