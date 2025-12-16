package com.example.calorialcalculator.Backend.api

import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class CalorieNinjasApi {

    private val apiKey = "xYlBqxtIPdWTk/Q5xCZRfg==tX9FGUZigiFPEdc9"
    private val baseUrl = "https://api.calorieninjas.com/v1/nutrition?query="

    fun query(
        query: String,
        onSuccess: (List<FoodItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(baseUrl + query)
                    .addHeader("X-Api-Key", apiKey)
                    .build()

                val response = client.newCall(request).execute()
                val json = response.body?.string()

                val result = Gson().fromJson(json, NutritionResponse::class.java)
                onSuccess(result.items)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }.start()
    }
}
