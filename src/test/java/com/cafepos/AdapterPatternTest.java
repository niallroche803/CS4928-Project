package com.cafepos;

import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import vendor.legacy.LegacyThermalPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdapterPatternTest {
    private LegacyThermalPrinter legacyPrinter;
    private Printer adapter;

    @BeforeEach
    void setUp() {
        legacyPrinter = new LegacyThermalPrinter();
        adapter = new LegacyPrinterAdapter(legacyPrinter);
    }

    @Test
    void testAdapterImplementsPrinterInterface() {
        assertTrue(adapter instanceof Printer);
    }

    @Test
    void testPrintReceiptDelegation() {
        String receipt = "Test Receipt\nTotal: $5.00";
        
        assertDoesNotThrow(() -> adapter.printReceipt(receipt));
    }

    @Test
    void testPrintReceiptWithEmptyString() {
        assertDoesNotThrow(() -> adapter.printReceipt(""));
    }

    @Test
    void testPrintReceiptWithSpecialCharacters() {
        String receipt = "Receipt with special chars: €£¥";
        
        assertDoesNotThrow(() -> adapter.printReceipt(receipt));
    }

    @Test
    void testAdapterWrapsLegacyPrinter() {
        LegacyPrinterAdapter concreteAdapter = new LegacyPrinterAdapter(legacyPrinter);
        
        assertDoesNotThrow(() -> concreteAdapter.printReceipt("Test"));
    }
}