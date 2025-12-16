package com.example.calorialcalculator.Backend.api

import com.example.calorialcalculator.Backend.BASE_URL
import com.example.calorialcalculator.Backend.HttpClient
import com.example.calorialcalculator.Backend.api.Login.LoginRequest
import com.example.calorialcalculator.Backend.api.Login.LoginResponse
import com.example.calorialcalculator.Backend.api.Login.RegisterRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object AuthApi {

    fun register(
        login: String,
        password: String,
        firstName: String,
        lastName: String,
        height: Int,
        weight: Int,
        activeLevel: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val json = HttpClient.gson.toJson(
            RegisterRequest(login, password, firstName, lastName, height,weight, activeLevel)
        )

        val request = Request.Builder()
            .url("$BASE_URL/auth/register")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        HttpClient.client.newCall(request)
            .enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message ?: "Network error")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onError("Login failed (${response.code})")
                        return
                    }

                    val body = response.body?.string() ?: return
                    val result = HttpClient.gson.fromJson(
                        body,
                        LoginResponse::class.java
                    )

                    onSuccess(result.userId)
                }
            })
    }

    fun login(
        login: String,
        password: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val json = HttpClient.gson.toJson(
            LoginRequest(login, password)
        )

        val request = Request.Builder()
            .url("$BASE_URL/auth/login")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        HttpClient.client.newCall(request)
            .enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message ?: "Network error")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onError("Login failed (${response.code})")
                        return
                    }

                    val body = response.body?.string() ?: return
                    val result = HttpClient.gson.fromJson(
                        body,
                        LoginResponse::class.java
                    )

                    onSuccess(result.userId.toInt())
                }
            })
    }

    fun delete(
        userId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url("$BASE_URL/auth/delete/$userId")
            .delete()
            .build()

        HttpClient.client.newCall(request)
            .enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message ?: "Network error")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onError("Delete failed (${response.code})")
                        return
                    }

                    onSuccess()
                }
            })
    }


    fun getProfile(
        userId: Int,
        onSuccess: (UserProfileDto) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url("$BASE_URL/auth/profile/$userId")
            .get()
            .build()

        HttpClient.client.newCall(request)
            .enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message ?: "Network error")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onError("Error ${response.code}")
                        return
                    }

                    val json = response.body?.string()
                        ?: run {
                            onError("Empty response")
                            return
                        }

                    val profile = HttpClient.gson.fromJson(
                        json,
                        UserProfileDto::class.java
                    )

                    onSuccess(profile)
                }
            })
    }


}

