package com.example.learningdashboard.domain

import com.example.myapplication.domain.model.ProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun percent_roundsToNearestWholeNumber_andHandlesEmptyCourse() {
        assertEquals(65, ProgressCalculator.percent(completed = 13, total = 20))
        assertEquals(38, ProgressCalculator.percent(completed = 6, total = 16))  // 37.5 rounds up
        assertEquals(0, ProgressCalculator.percent(completed = 0, total = 0))    // No divide-by-zero
    }
}