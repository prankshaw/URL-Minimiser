package com.craft.demo.commons.tester.utils;

import com.craft.demo.commons.utils.NextIdGenerator;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class IdGeneratorTest {

    private static final long MAX_SEQUENCE = ~(-1L << 12);

    @InjectMocks
    private NextIdGenerator nextIdGenerator = new NextIdGenerator();

    @Test
    public void duplicationCheck() {
        Set<Long> generatedIdsSet = new HashSet<>();
        int counter = 0;
        long idGenerated;

        while (counter <= MAX_SEQUENCE) {
            counter++;

            try {
                idGenerated = nextIdGenerator.nextId();

                if (generatedIdsSet.contains(idGenerated)) {
                    fail("Duplicate Ids Generated -> " + idGenerated + " on counter: " + counter);
                }

                generatedIdsSet.add(idGenerated);
            } catch (Exception e) {
                fail("Exception thrown -> " + e);
            }

        }

        assertNotNull(generatedIdsSet);
        assertNotEquals(0, generatedIdsSet.size());
        assertEquals(4096, generatedIdsSet.size());
    }

}
