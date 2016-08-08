package com.zeiss.quarkfx.logging;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoggerFactoryTest {

    @Test
    public void setLogMinimumLevel()
    {
        //Setup

        //Execute

        //Validate
        for(LogLevel l: LogLevel.values()) {
            Log.setMinimumLogLevel(l);
            assertEquals(l, Log.getMinimumLogLevel());
        }
    }


    @Test
    public void compareLogLevel() {
        //Setup

        //Execute
        LogLevel level0 = LogLevel.VERBOSE;
        LogLevel level1 = LogLevel.DEBUG;
        LogLevel level2 = LogLevel.INFO;
        LogLevel level3 = LogLevel.WARNING;
        LogLevel level4 = LogLevel.ERROR;

        //Validate
        assertTrue(level0.isGreaterEqual(level0));
        assertFalse(level0.isGreaterEqual(level1));
        assertFalse(level0.isGreaterEqual(level2));
        assertFalse(level0.isGreaterEqual(level3));
        assertFalse(level0.isGreaterEqual(level4));

        assertTrue(level1.isGreaterEqual(level0));
        assertTrue(level1.isGreaterEqual(level1));
        assertFalse(level1.isGreaterEqual(level2));
        assertFalse(level1.isGreaterEqual(level3));
        assertFalse(level1.isGreaterEqual(level4));

        assertTrue(level2.isGreaterEqual(level0));
        assertTrue(level2.isGreaterEqual(level1));
        assertTrue(level2.isGreaterEqual(level2));
        assertFalse(level2.isGreaterEqual(level3));
        assertFalse(level2.isGreaterEqual(level4));

        assertTrue(level3.isGreaterEqual(level0));
        assertTrue(level3.isGreaterEqual(level1));
        assertTrue(level3.isGreaterEqual(level2));
        assertTrue(level3.isGreaterEqual(level3));
        assertFalse(level3.isGreaterEqual(level4));

        assertTrue(level4.isGreaterEqual(level0));
        assertTrue(level4.isGreaterEqual(level1));
        assertTrue(level4.isGreaterEqual(level2));
        assertTrue(level4.isGreaterEqual(level3));
        assertTrue(level4.isGreaterEqual(level4));
    }
}