package com.example.echo

object BatteryPercentage {
    fun calculate(level: Int, scale: Int): Int {
        if (scale <= 0 || level < 0) return 0
        return ((level * 100) / scale).coerceIn(0, 100)
    }
}
