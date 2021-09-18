package com.example.briefact.registation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.briefact.R

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()
    }
}