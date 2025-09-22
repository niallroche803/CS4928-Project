package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.catalog.Catalog;
import com.cafepos.catalog.InMemoryCatalog;
import com.cafepos.catalog.SimpleProduct;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CatalogTest {
    
    @Test
    void catalog_add_and_find() {
        Catalog catalog = new InMemoryCatalog();
        SimpleProduct product = new SimpleProduct("P1", "Test Product", Money.of(2.50));
        
        catalog.add(product);
        var found = catalog.findById("P1");
        
        assertTrue(found.isPresent());
        assertEquals(product, found.get());
    }
    
    @Test
    void catalog_find_nonexistent() {
        Catalog catalog = new InMemoryCatalog();
        var found = catalog.findById("NONEXISTENT");
        assertFalse(found.isPresent());
    }
    
    @Test
    void catalog_null_product() {
        Catalog catalog = new InMemoryCatalog();
        assertThrows(IllegalArgumentException.class, () -> catalog.add(null));
    }
}