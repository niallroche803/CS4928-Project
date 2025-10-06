package com.cafepos;

import com.cafepos.domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderIdsTest {
    
    @Test
    void orderids_generates_sequential_ids() {
        long id1 = OrderIds.next();
        long id2 = OrderIds.next();
        
        assertTrue(id2 > id1);
        assertEquals(1, id2 - id1);
    }
    
    @Test
    void orderids_starts_above_1000() {
        long id = OrderIds.next();
        assertTrue(id > 1000);
    }
    
    @Test
    void orderids_thread_safe() throws InterruptedException {
        final int threadCount = 10;
        final int idsPerThread = 100;
        final long[] results = new long[threadCount * idsPerThread];
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    results[threadIndex * idsPerThread + j] = OrderIds.next();
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Check all IDs are unique
        for (int i = 0; i < results.length; i++) {
            for (int j = i + 1; j < results.length; j++) {
                assertNotEquals(results[i], results[j], "IDs should be unique");
            }
        }
    }
}