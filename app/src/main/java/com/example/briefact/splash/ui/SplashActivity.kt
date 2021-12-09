package com.example.briefact.splash.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.example.briefact.MainActivityJ
import com.example.briefact.MainFragment
import com.example.briefact.R
import com.example.briefact.registration.CheckActivity
import com.example.briefact.registration.CheckFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    lateinit var show_logo: Animation
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        show_logo = AnimationUtils.loadAnimation(application, R.anim.logo_splash)
        val layoutParams2 = splashActivityLogo.layoutParams
        splashActivityLogo.layoutParams = layoutParams2
        splashActivityLogo.startAnimation(show_logo)
        splashActivityLogo.isClickable = true
        auth = FirebaseAuth.getInstance()
        checkIfUserIsLoggedIn()
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