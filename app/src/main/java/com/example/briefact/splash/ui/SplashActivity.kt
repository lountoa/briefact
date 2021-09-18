package com.example.briefact.splash.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.briefact.MainActivity
import com.example.briefact.MainFragment
import com.example.briefact.R
import com.example.briefact.registation.RegistrationActivity
import com.example.briefact.registation.RegistrationFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()

        var mFirebaseUser: FirebaseUser? = mFirebaseAuth.currentUser
        if(mFirebaseUser != null) {
            val intent = Intent(this,MainFragment::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this,RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}