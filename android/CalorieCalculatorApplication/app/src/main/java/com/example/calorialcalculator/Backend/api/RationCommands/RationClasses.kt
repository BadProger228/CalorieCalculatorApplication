package com.example.calorialcalculator.Backend.api.RationCommands

data class CreateSavedRationRequest(
    val userId: Int,
    val name: String,
    val food: List<SavedRationItemDto>
)
data class SavedRationDto(
    val id: Int,
    val name: String,
    val createdAt: String,
    val totalCalories: Int
)
data class SavedRationItemDto(
    val foodName: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val weight_g: Double
)

data class DeleteRationRequest(
    val userId: Int,
    val rationName: String
)

data class SavedRationResponse(
    val id: Int,
    val name: String,
    val totalCalories: Int,
    val items: List<SavedRationItemDto>
)