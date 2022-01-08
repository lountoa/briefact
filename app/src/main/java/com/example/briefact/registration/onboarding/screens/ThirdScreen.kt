package com.example.briefact.registration.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.briefact.R
import kotlinx.android.synthetic.main.fragment_third_screen.view.*
import com.example.briefact.signInOrRegister.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.briefact.signInOrRegister.RegistrationActivity
import android.app.Activity

class ThirdScreen : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)

        view.signin.setOnClickListener {
            onBoardingFinished()
            moveToLoginActivity()
        }

        view.register.setOnClickListener {
            onBoardingFinished()
            moveToRegisterActivity()
        }

        mAuth = FirebaseAuth.getInstance()

        return view
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

    private fun moveToLoginActivity() {
        val i = Intent(activity, LoginActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(0, 0)
    }

    private fun moveToRegisterActivity() {
        val i = Intent(activity, RegistrationActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(0, 0)
    }
}