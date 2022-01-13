package com.example.briefact.splash.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.briefact.main.MainActivityJ
import com.example.briefact.R
import com.example.briefact.registration.CheckActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    lateinit var show_logo: Animation
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.d("Сейчас в", "SplashActivity")
        show_logo = AnimationUtils.loadAnimation(application, R.anim.logo_splash)
        val layoutParams2 = splashActivityLogo.layoutParams
        splashActivityLogo.layoutParams = layoutParams2
        splashActivityLogo.startAnimation(show_logo)
        splashActivityLogo.isClickable = true
        auth = FirebaseAuth.getInstance()
        val caller = intent.getStringExtra("back")

        if (caller == "restore") {
            checkIfUserIsLoggedIn()
        } else {
            checkIfUserIsLoggedIn()
        }
    }

    private fun checkIfUserIsLoggedIn() {
        val currentUser = auth.currentUser

        Log.d("ПРОВЕРКА НА ЛОГИН", (currentUser != null).toString())

        Handler().postDelayed({
            if (currentUser != null) {
                val intent = Intent(this, MainActivityJ::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, CheckActivity::class.java)
                startActivity(intent)
            }
        }, 2500)
    }
}