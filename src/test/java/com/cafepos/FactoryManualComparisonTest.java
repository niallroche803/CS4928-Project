package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.decorator.*;
import com.cafepos.domain.*;
import com.cafepos.factory.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FactoryManualComparisonTest {

    @Test
    void factory_vs_manual_construction() {
        
        Product viaFactory = new ProductFactory().create("ESP+SHOT+OAT+L");
        
        Product viaManual = new SizeLarge(new OatMilk(new ExtraShot(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)))));
        
        assertEquals(viaFactory.name(), viaManual.name());
        
        assertEquals(((Priced) viaFactory).price(), ((Priced) viaManual).price());
        
        Order order1 = new Order(1);
        Order order2 = new Order(2);
        
        order1.addItem(new LineItem(viaFactory, 1));
        order2.addItem(new LineItem(viaManual, 1));
        
        assertEquals(order1.subtotal(), order2.subtotal());
        
        assertEquals(order1.totalWithTax(10), order2.totalWithTax(10));
    }
}