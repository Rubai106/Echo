package com.example.echo

import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryPercentageTest {
    @Test
    fun calculatesPercentageFromLevelAndScale() {
        assertEquals(50, BatteryPercentage.calculate(50, 100))
        assertEquals(100, BatteryPercentage.calculate(100, 100))
        assertEquals(0, BatteryPercentage.calculate(0, 100))
    }

    @Test
    fun handlesInvalidScaleAndClampsValues() {
        assertEquals(0, BatteryPercentage.calculate(50, 0))
        assertEquals(0, BatteryPercentage.calculate(-1, 100))
        assertEquals(100, BatteryPercentage.calculate(150, 100))
    }
}
