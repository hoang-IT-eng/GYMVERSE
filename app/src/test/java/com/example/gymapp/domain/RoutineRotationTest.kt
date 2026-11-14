package com.example.gymapp.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class RoutineRotationTest {
    private val push = setOf(1L, 2L, 3L)
    private val pull = setOf(4L, 5L, 6L)
    private val legs = setOf(7L, 8L, 9L)
    private val days = listOf(push, pull, legs)

    @Test
    fun noHistoryStartsAtTheFirstDay() {
        assertEquals(0, RoutineRotation.nextDayIndex(days, emptyList()))
    }

    @Test
    fun suggestsTheDayAfterTheLastMatchedSession() {
        assertEquals(2, RoutineRotation.nextDayIndex(days, listOf(setOf(4L, 5L))))
    }

    @Test
    fun wrapsAroundAfterTheLastDay() {
        assertEquals(0, RoutineRotation.nextDayIndex(days, listOf(legs)))
    }

    @Test
    fun skipsFreeWorkoutsThatMatchNoDay() {
        assertEquals(1, RoutineRotation.nextDayIndex(days, listOf(setOf(99L), emptySet(), push)))
    }

    @Test
    fun picksTheDayWithTheLargestShareOfOverlap() {
        val pushB = setOf(1L, 10L, 11L, 12L)
        assertEquals(2, RoutineRotation.nextDayIndex(listOf(push, pushB, legs), listOf(setOf(1L, 10L, 11L))))
    }
}
