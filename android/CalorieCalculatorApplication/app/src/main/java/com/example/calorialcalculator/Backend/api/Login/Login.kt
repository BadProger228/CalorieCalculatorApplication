package com.example.calorialcalculator.Backend.api.Login

data class LoginRequest(
    val login: String,
    val password: String
)
data class LoginResponse(
    val userId: Int
)
data class RegisterRequest(
    val login: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val heightCm: Int,
    val weightKg: Int,
    val activityLevel: String
)