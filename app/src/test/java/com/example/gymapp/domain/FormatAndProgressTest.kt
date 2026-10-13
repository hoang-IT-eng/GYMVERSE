package com.example.gymapp.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatAndProgressTest {
    @Test
    fun formatsKilogramsAndAddedLoad() {
        assertEquals("60", formatKg(60.0))
        assertEquals("62.5", formatKg(62.5))
        assertEquals("60 kg", formatLoad(60.0, addedLoad = false))
        assertEquals("+0 kg", formatLoad(0.0, addedLoad = true))
        assertEquals("+5 kg", formatLoad(5.0, addedLoad = true))
        assertEquals("+2.5", formatSignedKg(2.5))
    }

    @Test
    fun summarizesRepeatedWeights() {
        val text = summarizeSets(
            listOf(
                SetPerformance(60.0, 10),
                SetPerformance(60.0, 10),
                SetPerformance(60.0, 8),
            ),
            addedLoad = false,
        )
        assertEquals("60 kg × 10, 10, 8", text)
    }

    @Test
    fun progressPointUsesTheSetThatProducesTheBestEstimate() {
        val points = progressPoints(
            listOf(
                SessionPerformance(
                    sessionId = 1,
                    startedAt = 10,
                    sets = listOf(
                        SetPerformance(100.0, 3),
                        SetPerformance(80.0, 10),
                    ),
                ),
            ),
        )
        assertEquals(110.0, points.single().bestE1rm!!, 0.01)
        assertEquals(100.0, points.single().e1rmWeightKg!!, 0.001)
        assertEquals(3, points.single().e1rmReps)
        assertEquals(10, points.single().bestReps)
        assertEquals(1100.0, points.single().volumeKg, 0.001)
    }
}
