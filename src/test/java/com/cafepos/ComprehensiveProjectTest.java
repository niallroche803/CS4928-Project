package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.checkout.*;
import com.cafepos.command.*;
import com.cafepos.common.Money;
import com.cafepos.decorator.*;
import com.cafepos.domain.*;
import com.cafepos.factory.*;
import com.cafepos.infra.Wiring;
import com.cafepos.menu.*;
import com.cafepos.observers.*;
import com.cafepos.payment.*;
import com.cafepos.pricing.*;
import com.cafepos.printing.*;
import com.cafepos.state.OrderFSM;
import com.cafepos.ui.*;
import com.cafepos.checkout.CheckoutService;
import vendor.legacy.LegacyThermalPrinter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class that validates all project features week-by-week.
 * Tests the complete system integration from Week 2 through Week 10.
 */
public class ComprehensiveProjectTest {
    
    private ProductFactory factory;
    private Catalog catalog;
    
    @BeforeEach
    void setUp() {
        factory = new ProductFactory();
        catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-LAT", "Latte", Money.of(3.20)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
    }
    
    @Test
    void week2_basic_order_and_pricing() {
        // Test basic order creation and pricing calculations
        Order order = new Order(OrderIds.next());
        order.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
        order.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
        
        TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
        
        assertEquals(2, order.items().size());
        assertEquals(Money.of(8.50), order.subtotal()); // 2*2.50 + 1*3.50
        assertEquals(Money.of(0.85), order.tax(taxPolicy)); // 10% of 8.50
        assertEquals(Money.of(9.35), order.totalWithTax(taxPolicy));
    }
    
    @Test
    void week3_payment_strategies() {
        Order order = new Order(OrderIds.next());
        order.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 1));
        
        // Test all payment strategies work without throwing exceptions
        assertDoesNotThrow(() -> new CashPayment().pay(order));
        assertDoesNotThrow(() -> new CardPayment("1234567812341234").pay(order));
        assertDoesNotThrow(() -> new WalletPayment("wallet-123").pay(order));
        
        // Test payment strategy properties
        CardPayment cardPayment = new CardPayment("1234567890123456");
        assertTrue(cardPayment.toString().contains("Card"));
        
        WalletPayment walletPayment = new WalletPayment("test-wallet");
        assertTrue(walletPayment.toString().contains("Wallet"));
    }
    
    @Test
    void week4_observer_pattern() {
        Order order = new Order(OrderIds.next());
        
        // Register observers
        KitchenDisplay kitchen = new KitchenDisplay();
        DeliveryDesk delivery = new DeliveryDesk();
        CustomerNotifier customer = new CustomerNotifier();
        
        order.register(kitchen);
        order.register(delivery);
        order.register(customer);
        
        // Test observer notifications don't throw exceptions
        order.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 1));
        assertDoesNotThrow(() -> order.markItemAdded());
        
        order.pay(new CashPayment());
        assertDoesNotThrow(() -> order.markPaid());
        
        assertDoesNotThrow(() -> order.markReady());
    }
    
    @Test
    void week5_factory_and_decorator() {
        // Test factory creates decorated products correctly
        Product espresso = factory.create("ESP");
        assertEquals("Espresso", espresso.name());
        assertEquals(Money.of(2.50), espresso.basePrice());
        
        Product decoratedEspresso = factory.create("ESP+SHOT+OAT");
        assertEquals("Espresso + Extra Shot + Oat Milk", decoratedEspresso.name());
        assertEquals(Money.of(3.80), ((Priced) decoratedEspresso).price()); // 2.50 + 0.80 + 0.50
        
        Product largeLatte = factory.create("LAT+L");
        assertEquals("Latte (Large)", largeLatte.name());
        assertEquals(Money.of(3.90), ((Priced) largeLatte).price()); // 3.20 + 0.70
        
        // Test complex decoration
        Product complex = factory.create("ESP+SHOT+OAT+SYP+L");
        assertTrue(complex.name().contains("Espresso"));
        assertTrue(complex.name().contains("Extra Shot"));
        assertTrue(complex.name().contains("Oat Milk"));
        assertTrue(complex.name().contains("Syrup"));
        assertTrue(complex.name().contains("(Large)"));
    }
    
    @Test
    void week6_refactored_architecture() {
        PricingService pricing = new PricingService(
            new LoyaltyPercentDiscount(5), 
            new FixedRateTaxPolicy(10)
        );
        ReceiptPrinter printer = new ReceiptPrinter();
        CheckoutService checkout = new CheckoutService(factory, pricing, printer, 10);

        String receipt = checkout.checkout("ESP+SHOT+OAT", 2, new CardPayment("1234567890123456"));
        
        assertTrue(receipt.contains("ESP+SHOT+OAT"));
        assertTrue(receipt.contains("x2"));
        
        // Test different discount policies
        PricingService fixedDiscount = new PricingService(
            new FixedCouponDiscount(Money.of(1.00)), 
            new FixedRateTaxPolicy(10)
        );
        CheckoutService checkoutFixed = new CheckoutService(factory, fixedDiscount, printer, 10);
        String receiptFixed = checkoutFixed.checkout("ESP", 1, new CashPayment());
        assertNotNull(receiptFixed);
    }
    
    @Test
    void week8_adapter_pattern() {
        String receipt = "Order (LAT+L) x2\nSubtotal: 7.80\nTax (10%): 0.78\nTotal: 8.58";
        
        // Test adapter works with legacy printer
        Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());
        assertDoesNotThrow(() -> printer.printReceipt(receipt));
        
        // Test regular receipt printer
        ReceiptPrinter regularPrinter = new ReceiptPrinter();
        assertDoesNotThrow(() -> regularPrinter.print(receipt));
    }
    
    @Test
    void week8_command_pattern() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(4);
        
        // Set up commands
        AddItemCommand addEspresso = new AddItemCommand(service, "ESP+SHOT+OAT", 1);
        AddItemCommand addLatte = new AddItemCommand(service, "LAT+L", 2);
        PayOrderCommand payOrder = new PayOrderCommand(service, new CardPayment("1234567890123456"), 10);
        MacroCommand orderMacro = new MacroCommand(addEspresso, addLatte);
        
        remote.setSlot(0, addEspresso);
        remote.setSlot(1, addLatte);
        remote.setSlot(2, payOrder);
        remote.setSlot(3, orderMacro);
        
        // Test individual commands
        remote.press(0); // Add espresso
        assertEquals(1, order.items().size());
        
        remote.press(1); // Add latte
        assertEquals(2, order.items().size());
        
        // Test undo
        remote.undo();
        assertEquals(1, order.items().size());
        
        // Test macro command
        Order newOrder = new Order(OrderIds.next());
        OrderService newService = new OrderService(newOrder);
        MacroCommand newMacro = new MacroCommand(
            new AddItemCommand(newService, "ESP", 1),
            new AddItemCommand(newService, "LAT", 1)
        );
        
        newMacro.execute();
        assertEquals(2, newOrder.items().size());
        
        newMacro.undo();
        assertEquals(0, newOrder.items().size());
    }
    
    @Test
    void week9_composite_menu() {
        Menu root = new Menu("CAFÉ MENU");
        Menu drinks = new Menu("Drinks");
        Menu coffee = new Menu("Coffee");
        Menu desserts = new Menu("Desserts");
        
        coffee.add(new MenuItem("Espresso", Money.of(2.50), true));
        coffee.add(new MenuItem("Latte (Large)", Money.of(3.90), true));
        drinks.add(coffee);
        desserts.add(new MenuItem("Cheesecake", Money.of(3.50), false));
        desserts.add(new MenuItem("Oat Cookie", Money.of(1.20), true));
        root.add(drinks);
        root.add(desserts);
        
        // Test menu structure
        assertEquals("CAFÉ MENU", root.name());
        assertTrue(root.allItems().size() > 4); // Includes submenus and items
        
        // Test vegetarian filtering
        var vegItems = root.vegetarianItems();
        assertEquals(3, vegItems.size()); // Espresso, Latte, Oat Cookie
        assertTrue(vegItems.stream().allMatch(MenuItem::vegetarian));
        assertFalse(vegItems.stream().anyMatch(item -> item.name().equals("Cheesecake")));
        
        // Test iterator functionality
        assertDoesNotThrow(() -> root.print());
    }
    
    @Test
    void week9_state_pattern() {
        OrderFSM fsm = new OrderFSM();
        
        // Test initial state
        assertEquals("NEW", fsm.status());
        
        // Test invalid transition (prepare before pay)
        fsm.prepare();
        assertEquals("NEW", fsm.status()); // Should remain in NEW
        
        // Test valid transitions
        fsm.pay();
        assertEquals("PREPARING", fsm.status());
        
        fsm.markReady();
        assertEquals("READY", fsm.status());
        
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
        
        // Test cancellation from preparing state
        OrderFSM fsm2 = new OrderFSM();
        fsm2.pay();
        fsm2.cancel();
        assertEquals("CANCELLED", fsm2.status());
    }
    
    @Test
    void week10_mvc_architecture() {
        var components = Wiring.createDefault();
        var controller = new OrderController(components.repo(), components.checkout());
        var view = new ConsoleView();
        
        long orderId = 4101L;
        
        // Test controller operations
        controller.createOrder(orderId);
        controller.addItem(orderId, "ESP+SHOT+OAT", 1);
        controller.addItem(orderId, "LAT+L", 2);
        
        String receipt = controller.checkout(orderId, 10);
        
        // Verify receipt content
        assertNotNull(receipt);
        assertTrue(receipt.contains("Order #"));
        
        // Test view can display receipt
        assertDoesNotThrow(() -> view.print(receipt));
        
        // Test repository integration
        assertTrue(components.repo().findById(orderId).isPresent());
    }
    
    @Test
    void full_system_integration_test() {
        // Complete end-to-end workflow testing all patterns together
        
        // 1. Create menu (Composite)
        Menu menu = new Menu("Full Menu");
        menu.add(new MenuItem("ESP", Money.of(2.50), true));
        menu.add(new MenuItem("LAT", Money.of(3.20), true));
        
        // 2. Create order with observers
        Order order = new Order(OrderIds.next());
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        
        // 3. Use factory to create decorated products
        Product decoratedProduct = factory.create("ESP+SHOT+OAT");
        order.addItem(new LineItem(decoratedProduct, 1));
        
        // 4. Use command pattern for operations
        OrderService service = new OrderService(order);
        AddItemCommand addCommand = new AddItemCommand(service, "LAT+L", 1);
        addCommand.execute();
        
        // 5. Use state machine
        OrderFSM fsm = new OrderFSM();
        fsm.pay();
        fsm.markReady();
        
        // 6. Use pricing service with strategies
        PricingService pricing = new PricingService(
            new LoyaltyPercentDiscount(10), 
            new FixedRateTaxPolicy(8)
        );
        
        // 7. Process payment with strategy
        PaymentStrategy payment = PaymentStrategyFactory.create("CARD", "1234567890123456");
        CheckoutService checkoutService = new CheckoutService(factory, pricing, new ReceiptPrinter(), 8);
        String receipt = checkoutService.checkout("ESP+SHOT", 1, payment);
        
        // Verify integration
        assertNotNull(receipt);
        assertTrue(receipt.contains("ESP+SHOT"));
        assertEquals(2, order.items().size());
        assertEquals("READY", fsm.status());
        
        // Test MVC integration
        var components = Wiring.createDefault();
        var controller = new OrderController(components.repo(), components.checkout());
        
        long mvcOrderId = 5001L;
        controller.createOrder(mvcOrderId);
        controller.addItem(mvcOrderId, "LAT+L+SHOT", 1);
        String mvcReceipt = controller.checkout(mvcOrderId, 10);
        
        assertNotNull(mvcReceipt);
        assertTrue(mvcReceipt.contains("Order #"));
    }
    
    @Test
    void all_factories_integration() {
        // Test all factory classes work together
        ProductFactory productFactory = new ProductFactory();
        
        // Create products
        Product p1 = productFactory.create("ESP+SHOT+OAT+SYP+L");
        Product p2 = productFactory.create("LAT");
        
        // Create payment strategies
        PaymentStrategy card = PaymentStrategyFactory.create("CARD", "1234567890123456");
        PaymentStrategy cash = PaymentStrategyFactory.create("CASH", null);
        PaymentStrategy wallet = PaymentStrategyFactory.create("WALLET", "wallet-id");
        
        // Create discount policies
        DiscountPolicy loyalty = DiscountPolicyFactory.create("LOYAL5");
        DiscountPolicy coupon = DiscountPolicyFactory.create("COUPON1");
        DiscountPolicy none = DiscountPolicyFactory.create("NONE");
        
        // Verify all objects created successfully
        assertNotNull(p1);
        assertNotNull(p2);
        assertNotNull(card);
        assertNotNull(cash);
        assertNotNull(wallet);
        assertNotNull(loyalty);
        assertNotNull(coupon);
        assertNotNull(none);
        
        // Test they work together
        Order order = new Order(OrderIds.next());
        order.addItem(new LineItem(p1, 1));
        order.addItem(new LineItem(p2, 2));
        
        assertDoesNotThrow(() -> card.pay(order));
        
        Money subtotal = order.subtotal();
        assertTrue(loyalty.discountOf(subtotal).compareTo(Money.zero()) >= 0);
        assertTrue(coupon.discountOf(subtotal).compareTo(Money.zero()) >= 0);
        assertEquals(Money.zero(), none.discountOf(subtotal));
    }
}