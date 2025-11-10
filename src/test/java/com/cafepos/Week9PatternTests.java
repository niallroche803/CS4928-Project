package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.menu.*;
import com.cafepos.state.OrderFSM;
import com.cafepos.decorator.Priced;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Week9PatternTests {

    // ========== COMPOSITE/ITERATOR TESTS ==========
    
    @Test
    void depth_first_iteration_collects_all_nodes() {
        Menu root = new Menu("ROOT");
        Menu a = new Menu("A");
        Menu b = new Menu("B");
        root.add(a);
        root.add(b);
        a.add(new MenuItem("x", Money.of(1.0), true));
        b.add(new MenuItem("y", Money.of(2.0), false));

        List<String> names = root.allItems().stream().map(MenuComponent::name).toList();
        assertTrue(names.contains("x"));
        assertTrue(names.contains("y"));
    }

    @Test
    void vegetarian_items_filter_correctly() {
        Menu root = new Menu("ROOT");
        Menu beverages = new Menu("Beverages");
        Menu food = new Menu("Food");
        
        root.add(beverages);
        root.add(food);
        
        beverages.add(new MenuItem("Coffee", Money.of(3.0), true));
        beverages.add(new MenuItem("Milk", Money.of(2.0), true));
        food.add(new MenuItem("Burger", Money.of(8.0), false));
        food.add(new MenuItem("Salad", Money.of(6.0), true));

        List<MenuItem> vegItems = root.vegetarianItems();
        assertEquals(3, vegItems.size());
        assertTrue(vegItems.stream().allMatch(MenuItem::vegetarian));
        assertTrue(vegItems.stream().anyMatch(item -> item.name().equals("Coffee")));
        assertTrue(vegItems.stream().anyMatch(item -> item.name().equals("Salad")));
        assertFalse(vegItems.stream().anyMatch(item -> item.name().equals("Burger")));
    }

    @Test
    void depth_first_traversal_order() {
        Menu root = new Menu("ROOT");
        Menu a = new Menu("A");
        Menu b = new Menu("B");
        Menu c = new Menu("C");
        
        root.add(a);
        root.add(b);
        a.add(c);
        a.add(new MenuItem("item1", Money.of(1.0), true));
        c.add(new MenuItem("item2", Money.of(2.0), false));
        b.add(new MenuItem("item3", Money.of(3.0), true));

        List<String> names = root.allItems().stream().map(MenuComponent::name).toList();
        
        // Verify all items are collected
        assertEquals(6, names.size()); // A, C, item1, item2, B, item3
        assertTrue(names.contains("A"));
        assertTrue(names.contains("B"));
        assertTrue(names.contains("C"));
        assertTrue(names.contains("item1"));
        assertTrue(names.contains("item2"));
        assertTrue(names.contains("item3"));
    }

    // ========== STATE PATTERN TESTS ==========

    @Test
    void order_fsm_happy_path() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());
        
        fsm.pay();
        assertEquals("PREPARING", fsm.status());
        
        fsm.markReady();
        assertEquals("READY", fsm.status());
        
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
    }

    @Test
    void illegal_transitions_print_messages() {
        OrderFSM fsm = new OrderFSM();
        
        // Capture system output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // Try invalid transition: prepare before pay
            fsm.prepare();
            assertEquals("NEW", fsm.status()); // Should remain in NEW state
            
            String output = outputStream.toString();
            assertTrue(output.contains("Cannot prepare") || output.contains("Invalid"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void state_transitions_with_cancel() {
        OrderFSM fsm = new OrderFSM();
        
        fsm.pay();
        assertEquals("PREPARING", fsm.status());
        
        fsm.cancel();
        assertEquals("CANCELLED", fsm.status());
    }

    @Test
    void invalid_transitions_from_delivered() {
        OrderFSM fsm = new OrderFSM();
        
        // Complete the happy path
        fsm.pay();
        fsm.markReady();
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
        
        // Try invalid operations on delivered order
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            fsm.pay();
            assertEquals("DELIVERED", fsm.status()); // Should remain delivered
            
            fsm.markReady();
            assertEquals("DELIVERED", fsm.status()); // Should remain delivered
        } finally {
            System.setOut(originalOut);
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    void integration_menu_factory_money() {
        // Create menu with items
        Menu root = new Menu("Cafe Menu");
        Menu beverages = new Menu("Beverages");
        root.add(beverages);
        
        beverages.add(new MenuItem("ESP", Money.of(2.50), true));
        beverages.add(new MenuItem("LAT", Money.of(3.20), true));
        
        // Find item by name
        MenuItem espresso = root.allItems().stream()
            .filter(item -> item instanceof MenuItem)
            .map(item -> (MenuItem) item)
            .filter(item -> item.name().equals("ESP"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(espresso);
        
        // Create product via factory
        ProductFactory factory = new ProductFactory();
        var product = factory.create("ESP+SHOT+L");
        
        // Assert money calculations match
        Money basePrice = Money.of(2.50);
        Money expectedTotal = basePrice.add(Money.of(0.80)).add(Money.of(0.70)); // ESP + SHOT + Large
        
        assertEquals(expectedTotal, ((Priced) product).price());
        assertEquals(espresso.price(), basePrice);
    }

    @Test
    void integration_vegetarian_filter_with_factory() {
        Menu root = new Menu("Menu");
        root.add(new MenuItem("ESP", Money.of(2.50), true));
        root.add(new MenuItem("LAT", Money.of(3.20), true));
        root.add(new MenuItem("MEAT_SANDWICH", Money.of(8.00), false));
        
        List<MenuItem> vegItems = root.vegetarianItems();
        assertEquals(2, vegItems.size());
        
        // Verify all vegetarian items can be created via factory
        ProductFactory factory = new ProductFactory();
        for (MenuItem item : vegItems) {
            if (item.name().equals("ESP") || item.name().equals("LAT")) {
                var product = factory.create(item.name());
                assertNotNull(product);
                assertEquals(item.price(), product.basePrice());
            }
        }
    }

    @Test
    void integration_state_with_menu_selection() {
        // Create order FSM
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());
        
        // Select items from menu
        Menu menu = new Menu("Menu");
        menu.add(new MenuItem("LAT", Money.of(3.20), true));
        
        MenuItem selectedItem = menu.allItems().stream()
            .filter(item -> item instanceof MenuItem)
            .map(item -> (MenuItem) item)
            .findFirst()
            .orElse(null);
        
        assertNotNull(selectedItem);
        
        // Process order through states
        fsm.pay();
        assertEquals("PREPARING", fsm.status());
        
        fsm.markReady();
        assertEquals("READY", fsm.status());
        
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
        
        // Verify item properties
        assertTrue(selectedItem.vegetarian());
        assertEquals(Money.of(3.20), selectedItem.price());
    }
}