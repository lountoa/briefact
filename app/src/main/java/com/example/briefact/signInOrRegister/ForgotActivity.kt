package com.example.briefact.signInOrRegister

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.widget.Toast
import com.example.briefact.R
import com.example.briefact.databinding.ActivityForgotBinding
import com.example.briefact.main.MainActivityJ
import com.example.briefact.registration.CheckActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.wait))
        progressDialog.setCanceledOnTouchOutside(false)

        binding.goBack.setOnClickListener {
            onBackPressed()
        }

        binding.recoveryButton.setOnClickListener {
            validateData()
        }
    }

    private var email = ""
    private fun validateData() {
        email = binding.recoveryEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_LONG).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.wrong_email), Toast.LENGTH_SHORT).show()
        } else {
            recoverPassword()
        }
    }

    private fun recoverPassword() {

        progressDialog.setMessage(getString(R.string.sending) + " $email")
        progressDialog.show()

        Handler().postDelayed({
            progressDialog.dismiss()
        }, 3000)

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                Toast.makeText(this, getString(R.string.sent), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, getString(R.string.not_sent), Toast.LENGTH_SHORT).show()
            }
    }

}