package com.example.calorialcalculator.Backend

import com.google.gson.Gson
import okhttp3.OkHttpClient

object HttpClient {
    val client = OkHttpClient()
    val gson = Gson()
}