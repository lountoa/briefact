package com.example.briefact.signInOrRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.briefact.MainActivity
import com.example.briefact.MainFragment
import com.example.briefact.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        auth = FirebaseAuth.getInstance()
        registerButton.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        var email = registerMail.text.toString()
        var password = registerPassword.text.toString()
        var cPassword = registerPasswordRequire.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && cPassword.isNotEmpty()) {
            if (password == cPassword) {
                saveUser(email, password)
            } else {
                Toast.makeText(this, R.string.passwordWarning, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, R.string.registerFailedPasOrEmail, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUser(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                checkResult(it.isSuccessful)
            }
    }

    private fun checkResult(isSuccess: Boolean) {
        if (isSuccess) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show()
        }
    }
}