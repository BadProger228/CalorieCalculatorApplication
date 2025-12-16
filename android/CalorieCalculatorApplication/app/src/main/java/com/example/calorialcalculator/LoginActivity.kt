package com.example.calorialcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calorialcalculator.Backend.api.AuthApi

class LoginActivity : AppCompatActivity() {

    lateinit var etLogin: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var btnGoRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etLogin = findViewById(R.id.etLogin)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoRegister = findViewById(R.id.btnGoRegister)

        btnLogin.setOnClickListener {

            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AuthApi.login(
                login,
                password,
                onSuccess = { user ->

                    runOnUiThread {
                        // 👉 Переход на MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("userId", user)
                        startActivity(intent)
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

        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}

