package com.cafepos;

import com.cafepos.command.*;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.CardPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandPatternTest {
    private OrderService service;
    private Order order;
    private PosRemote remote;

    @BeforeEach
    void setUp() {
        order = new Order(OrderIds.next());
        service = new OrderService(order);
        remote = new PosRemote(5);
    }

    @Test
    void testAddItemCommand() {
        AddItemCommand cmd = new AddItemCommand(service, "ESP", 2);
        cmd.execute();
        
        assertEquals(1, order.items().size());
        assertEquals(2, order.items().get(0).quantity());
    }

    @Test
    void testAddItemCommandUndo() {
        AddItemCommand cmd = new AddItemCommand(service, "LAT", 1);
        cmd.execute();
        assertEquals(1, order.items().size());
        
        cmd.undo();
        assertEquals(0, order.items().size());
    }

    @Test
    void testPayOrderCommand() {
        service.addItem("ESP", 1);
        PayOrderCommand payCmd = new PayOrderCommand(service, new CashPayment(), 10);
        
        assertDoesNotThrow(() -> payCmd.execute());
    }

    @Test
    void testPosRemoteSlotBinding() {
        AddItemCommand addCmd = new AddItemCommand(service, "CAP", 1);
        remote.setSlot(0, addCmd);
        
        remote.press(0);
        assertEquals(1, order.items().size());
    }

    @Test
    void testPosRemoteUndo() {
        AddItemCommand addCmd = new AddItemCommand(service, "ESP", 1);
        remote.setSlot(0, addCmd);
        
        remote.press(0);
        assertEquals(1, order.items().size());
        
        remote.undo();
        assertEquals(0, order.items().size());
    }

    @Test
    void testMacroCommand() {
        AddItemCommand add1 = new AddItemCommand(service, "ESP", 1);
        AddItemCommand add2 = new AddItemCommand(service, "LAT", 2);
        PayOrderCommand pay = new PayOrderCommand(service, new CardPayment("1234567890123456"), 10);
        
        MacroCommand macro = new MacroCommand(add1, add2, pay);
        macro.execute();
        
        assertEquals(2, order.items().size());
    }

    @Test
    void testMacroCommandUndo() {
        AddItemCommand add1 = new AddItemCommand(service, "ESP", 1);
        AddItemCommand add2 = new AddItemCommand(service, "LAT", 1);
        
        MacroCommand macro = new MacroCommand(add1, add2);
        macro.execute();
        assertEquals(2, order.items().size());
        
        macro.undo();
        assertEquals(0, order.items().size());
    }

    @Test
    void testEmptySlotPress() {
        remote.press(0); // Should handle gracefully
        assertEquals(0, order.items().size());
    }

    @Test
    void testUndoWithEmptyHistory() {
        assertDoesNotThrow(() -> remote.undo());
    }
}