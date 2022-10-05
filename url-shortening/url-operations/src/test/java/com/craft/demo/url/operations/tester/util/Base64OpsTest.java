package com.craft.demo.url.operations.tester.util;

import com.craft.demo.url.operations.utils.Base64Ops;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Base64OpsTest {

    @Test
    public void encode() {
        assertEquals("a", Base64Ops.encode(0));
        assertEquals("______", Base64Ops.encode((long) Math.pow(64, 6) - 1));
    }

    @Test
    public void decode() {
        assertEquals(0L, Base64Ops.decode("a"));
        assertEquals((long) Math.pow(64, 6) - 1, Base64Ops.decode("______"));
    }

}