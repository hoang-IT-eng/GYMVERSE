package com.example.gymapp.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OneRmTest {
    @Test
    fun epleyMatchesTheKnownTenRepValue() {
        val value = epley1Rm(100.0, 10)
        assertEquals(133.333, value!!, 0.01)
    }

    @Test
    fun singleRepIsTheWeightItself() {
        assertEquals(80.0, epley1Rm(80.0, 1)!!, 0.001)
    }

    @Test
    fun skipsHighRepsAndEmptyLoad() {
        assertNull(epley1Rm(20.0, 15))
        assertNull(epley1Rm(0.0, 8))
    }

    @Test
    fun bestEstimatePrefersTheHeavierLowRepSet() {
        val best = bestE1rm(
            listOf(
                SetPerformance(80.0, 10),
                SetPerformance(100.0, 5),
            ),
        )
        assertEquals(116.667, best!!, 0.01)
    }

    @Test
    fun recordHolderIsTheEarliestSetAtTheCurrentBest() {
        val first = set(1, 100.0, 5, 1_000)
        val second = set(2, 90.0, 5, 2_000)
        val third = set(3, 100.0, 5, 3_000)
        assertEquals(1L, recordHolderId(listOf(first, second, third)))
    }

    @Test
    fun aLoneSetIsNotMarkedAsARecord() {
        assertNull(recordHolderId(listOf(set(1, 100.0, 5, 1_000))))
    }

    private fun set(id: Long, weight: Double, reps: Int, at: Long) = SetEntry(
        id = id,
        sessionId = 1,
        exerciseId = 1,
        setNumber = id.toInt(),
        weightKg = weight,
        reps = reps,
        completedAt = at,
    )
}
