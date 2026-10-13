package com.example.gymapp.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressiveOverloadTest {
    @Test
    fun increasesWeightWhenEveryTopSetHitsTheCeiling() {
        val suggestion = ProgressiveOverload.suggest(
            lastSessionSets = listOf(
                SetPerformance(60.0, 10),
                SetPerformance(60.0, 10),
                SetPerformance(60.0, 10),
            ),
            repMin = 6,
            repMax = 10,
            incrementKg = 2.5,
            startKg = 20.0,
        )
        assertEquals(62.5, suggestion.weightKg, 0.001)
        assertEquals(6, suggestion.targetReps)
        assertTrue(suggestion.hasHistory)
    }

    @Test
    fun stepsTheWeakestSetUpByOneRep() {
        val suggestion = ProgressiveOverload.suggest(
            listOf(
                SetPerformance(60.0, 10),
                SetPerformance(60.0, 10),
                SetPerformance(60.0, 8),
            ),
            repMin = 6,
            repMax = 10,
            incrementKg = 2.5,
            startKg = 20.0,
        )
        assertEquals(60.0, suggestion.weightKg, 0.001)
        assertEquals(9, suggestion.targetReps)
    }

    @Test
    fun lighterBackoffSetsDoNotBlockAnIncrease() {
        val suggestion = ProgressiveOverload.suggest(
            listOf(
                SetPerformance(100.0, 8),
                SetPerformance(100.0, 8),
                SetPerformance(90.0, 5),
            ),
            repMin = 5,
            repMax = 8,
            incrementKg = 5.0,
            startKg = 20.0,
        )
        assertEquals(105.0, suggestion.weightKg, 0.001)
        assertEquals(5, suggestion.targetReps)
    }

    @Test
    fun decreasesWeightWhenEveryTopSetMissesTheFloor() {
        val suggestion = ProgressiveOverload.suggest(
            listOf(SetPerformance(80.0, 3), SetPerformance(80.0, 4)),
            repMin = 5,
            repMax = 8,
            incrementKg = 5.0,
            startKg = 20.0,
        )
        assertEquals(75.0, suggestion.weightKg, 0.001)
        assertEquals(5, suggestion.targetReps)
    }

    @Test
    fun ignoresACollapsedSetWhenChoosingTheNextRep() {
        val suggestion = ProgressiveOverload.suggest(
            listOf(
                SetPerformance(60.0, 8),
                SetPerformance(60.0, 8),
                SetPerformance(60.0, 3),
            ),
            repMin = 6,
            repMax = 10,
            incrementKg = 2.5,
            startKg = 20.0,
        )
        assertEquals(60.0, suggestion.weightKg, 0.001)
        assertEquals(9, suggestion.targetReps)
    }

    @Test
    fun doesNotDropAddedWeightBelowZero() {
        val suggestion = ProgressiveOverload.suggest(
            listOf(SetPerformance(0.0, 2)),
            repMin = 5,
            repMax = 10,
            incrementKg = 2.5,
            startKg = 0.0,
        )
        assertEquals(0.0, suggestion.weightKg, 0.001)
        assertEquals(5, suggestion.targetReps)
    }

    @Test
    fun firstSessionUsesTheStartingWeight() {
        val suggestion = ProgressiveOverload.suggest(
            emptyList(),
            repMin = 8,
            repMax = 12,
            incrementKg = 2.5,
            startKg = 20.0,
        )
        assertEquals(20.0, suggestion.weightKg, 0.001)
        assertEquals(8, suggestion.targetReps)
        assertFalse(suggestion.hasHistory)
    }
}
