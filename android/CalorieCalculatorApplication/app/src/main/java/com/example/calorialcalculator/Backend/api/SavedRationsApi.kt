package com.example.calorialcalculator.Backend.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.calorialcalculator.Backend.BASE_URL
import com.example.calorialcalculator.Backend.HttpClient
import com.example.calorialcalculator.Backend.HttpClient.gson
import com.example.calorialcalculator.Backend.api.RationCommands.CreateSavedRationRequest
import com.example.calorialcalculator.Backend.api.RationCommands.DeleteRationRequest
import com.example.calorialcalculator.Backend.api.RationCommands.SavedRationDto
import com.example.calorialcalculator.Backend.api.RationCommands.SavedRationItemDto
import com.example.calorialcalculator.Backend.api.RationCommands.SavedRationResponse
import okhttp3.Callback
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import okhttp3.Call

object SavedRationsApi {

    fun create(
        userId: Int,
        rationName: String,
        items: List<SavedRationItemDto>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val body = HttpClient.gson.toJson(
            CreateSavedRationRequest(userId, rationName, items)
        )

        val request = Request.Builder()
            .url("$BASE_URL/saved-rations/create")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        HttpClient.client.newCall(request)
            .enqueue(simpleCallback(onSuccess, onError))
    }

    fun list(
        userId: Int,
        onSuccess: (List<SavedRationDto>) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url("$BASE_URL/saved-rations/list?userId=$userId")
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

                    val json = response.body?.string() ?: return
                    val list = HttpClient.gson.fromJson(
                        json,
                        Array<SavedRationDto>::class.java
                    ).toList()

                    onSuccess(list)
                }
            })
    }

    fun getById(
        id: Int,
        onSuccess: (SavedRationResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url("$BASE_URL/saved-rations/$id")
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

                    val json = response.body?.string() ?: return
                    val response = gson.fromJson(
                        json,
                        SavedRationResponse::class.java
                    )

                    onSuccess(response)
                }
            })
    }

    fun delete(
        userId: Int,
        name: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url("$BASE_URL/saved-rations/delete?userId=$userId&name=$name")
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



}

