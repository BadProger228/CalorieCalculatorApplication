package com.example.calorialcalculator.Backend.api

data class User(
    val id : Int
)

data class UserProfileDto(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val heightCm: Int,
    val weightKg: Int,
    val activityLevel: String
)