package com.example.calorialcalculator

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calorialcalculator.Backend.api.AuthApi

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val spActivity = findViewById<Spinner>(R.id.spActivity)

        spActivity.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Low", "Medium", "High")
        )

        findViewById<Button>(R.id.btnRegister).setOnClickListener {

            val login = etLogin.text.toString()
            val password = etPassword.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val height = etHeight.text.toString().toIntOrNull()
            val weight = etWeight.text.toString().toIntOrNull()
            val activityLevel = spActivity.selectedItem.toString()

            if (height == null || weight == null) {
                Toast.makeText(this, "Height / Weight invalid", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            AuthApi.register(
                login,
                password,
                firstName,
                lastName,
                height,
                weight,
                activityLevel,
                onSuccess = {
                    runOnUiThread {
                        Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                },
                onError = { error ->
                    runOnUiThread {
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}
