package com.example.calorialcalculator.Backend.api

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

fun simpleCallback(
    onSuccess: () -> Unit,
    onError: (String) -> Unit
): Callback = object : Callback {

    override fun onFailure(call: Call, e: IOException) {
        onError(e.message ?: "Network error")
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {
            onSuccess()
        } else {
            onError("Error ${response.code}")
        }
    }
}

