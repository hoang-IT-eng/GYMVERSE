package com.example.gymapp.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VietFoodDbTest {
    @Test
    fun matchIgnoresAccentsCaseAndAliases() {
        assertEquals("Phở bò", VietFoodDb.match("PHO BO TAI")?.name)
        assertEquals("Chả giò", VietFoodDb.match("nem rán")?.name)
        assertEquals("Đậu hũ", VietFoodDb.match("dau phu")?.name)
    }

    @Test
    fun matchIsExactSoMixedNamesFallBackToTheAi() {
        assertNull(VietFoodDb.match("cơm chiên hải sản"))
    }

    @Test
    fun enrichKeepsGramsAndNameButUsesTableValues() {
        val guess = FoodItem("Ức gà luộc", 180.0, 120.0, 20.0, 0.0, 2.0)
        val enriched = VietFoodDb.enrich(guess)
        assertEquals("Ức gà luộc", enriched.name)
        assertEquals(180.0, enriched.grams, 0.001)
        assertEquals(31.0, enriched.proteinPer100g, 0.001)
        assertTrue(enriched.fromDatabase)
        assertFalse(VietFoodDb.enrich(FoodItem("Món lạ", 100.0, 100.0, 1.0, 1.0, 1.0)).fromDatabase)
    }

    @Test
    fun searchRanksPrefixMatchesFirst() {
        val results = VietFoodDb.search("bun")
        assertTrue(results.isNotEmpty())
        assertTrue(VietFoodDb.normalize(results.first().name).startsWith("bun"))
    }

    @Test
    fun everyEntryIsPlausible() {
        val alcoholic = setOf("Bia")
        val off = VietFoodDb.foods.filter { it.name !in alcoholic }.mapNotNull { food ->
            val fromMacros = food.protein * 4 + food.carbs * 4 + food.fat * 9
            if (kotlin.math.abs(fromMacros - food.kcal) <= food.kcal * 0.15 + 8) null
            else "${food.name}: ${food.kcal} vs $fromMacros"
        }
        assertTrue(off.joinToString("\n"), off.isEmpty())
        assertTrue(VietFoodDb.foods.all { it.portionGrams > 0 })
        assertEquals(VietFoodDb.foods.size, VietFoodDb.foods.map { it.name }.distinct().size)
    }
}
