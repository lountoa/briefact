package com.example.briefact.splash.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import com.example.briefact.MainActivity
import com.example.briefact.MainFragment
import com.example.briefact.R
import com.example.briefact.registration.CheckActivity
import com.example.briefact.registration.CheckFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = FirebaseAuth.getInstance()
        checkIfUserIsLoggedIn()
    }

    private fun checkIfUserIsLoggedIn() {
        val currentUser = auth.currentUser

        Log.d("ПРОВЕРКА НА ЛОГИН", (currentUser != null).toString())

        Handler().postDelayed({
            if (currentUser != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, CheckActivity::class.java)
                startActivity(intent)
            }
        }, 2000)
    }
}