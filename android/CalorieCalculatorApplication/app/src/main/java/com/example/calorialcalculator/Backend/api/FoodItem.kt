package com.example.calorialcalculator.Backend.api

data class NutritionResponse(
    val items: List<FoodItem>
)
data class FoodItem (
    val name: String,
    val calories: Double,
    val protein_g: Double,
    val fat_total_g: Double,
    val carbohydrates_total_g: Double,
    val sugar_g: Double
)