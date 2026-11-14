package com.example.gymapp.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionTest {
    @Test
    fun scalesPer100gValuesByPortion() {
        val rice = FoodItem("Cơm trắng", grams = 250.0, kcalPer100g = 130.0, proteinPer100g = 2.7, carbsPer100g = 28.0, fatPer100g = 0.3)
        assertEquals(325.0, rice.macros.kcal, 0.001)
        assertEquals(6.75, rice.macros.proteinG, 0.001)
    }

    @Test
    fun sumsEveryItemOfAMeal() {
        val items = listOf(
            FoodItem("Cơm", 200.0, 130.0, 2.7, 28.0, 0.3),
            FoodItem("Ức gà", 150.0, 165.0, 31.0, 0.0, 3.6),
        )
        val total = items.totalMacros()
        assertEquals(507.5, total.kcal, 0.001)
        assertEquals(51.9, total.proteinG, 0.001)
    }

    @Test
    fun katchMcArdleTargetsForLeanBulk() {
        val targets = nutritionTargets(weightKg = 75.0, bodyFatPct = 15.0, goal = NutritionGoal.LEAN_BULK)
        // LBM 63.75 kg -> BMR 1747 -> TDEE 2708 -> +250
        assertEquals(2958.0, targets.kcal, 1.0)
        assertEquals(150.0, targets.proteinG, 0.001)
        assertEquals(82.2, targets.fatG, 0.1)
        assertEquals(404.6, targets.carbsG, 0.5)
    }

    @Test
    fun cutLowersCaloriesAndRaisesProtein() {
        val bulk = nutritionTargets(75.0, 15.0, NutritionGoal.LEAN_BULK)
        val cut = nutritionTargets(75.0, 15.0, NutritionGoal.CUT)
        assertEquals(650.0, bulk.kcal - cut.kcal, 0.001)
        assertEquals(165.0, cut.proteinG, 0.001)
    }

    @Test
    fun kcalRangeRoundsToTens() {
        assertEquals(450..750, kcalRange(600.0))
    }

    @Test
    fun energyBreakdownShowsEachStep() {
        val profile = BodyProfile(weightKg = 75.0, bodyFatPct = 15.0, heightCm = 175, ageYears = 25, sex = Sex.MALE)
        val energy = energyBreakdown(profile, ActivityLevel.MODERATE, NutritionGoal.LEAN_BULK)
        assertEquals(1747.0, energy.bmrKatch, 0.5)
        // 750 + 1093.75 - 125 + 5
        assertEquals(1723.75, energy.bmrMifflin, 0.001)
        assertEquals(energy.bmrKatch * 1.55, energy.tdee, 0.001)
        assertEquals(energy.tdee + 250, energy.targetKcal, 0.001)
        assertEquals(nutritionTargets(75.0, 15.0, NutritionGoal.LEAN_BULK).kcal, energy.targetKcal, 0.001)
    }

    @Test
    fun mifflinSubtractsForFemales() {
        val male = mifflinStJeorBmr(60.0, 165, 30, Sex.MALE)
        val female = mifflinStJeorBmr(60.0, 165, 30, Sex.FEMALE)
        assertEquals(166.0, male - female, 0.001)
    }
}
