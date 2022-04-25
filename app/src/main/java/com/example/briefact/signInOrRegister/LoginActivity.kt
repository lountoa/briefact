package com.example.briefact.signInOrRegister

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.briefact.main.MainActivityJ
import com.example.briefact.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_check.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
            finish()
        }
        auth = FirebaseAuth.getInstance()

        loginSignInButton.setOnClickListener {
            getUserData()
            val inputManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                this.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun getUserData() {
        var email = loginEmail.text.toString()
        var password = loginPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            authUser(email, password)
        } else {
            Toast.makeText(this, R.string.registerFailedPasOrEmail, Toast.LENGTH_LONG).show()
        }
    }

    private fun authUser(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                checkResult(it.isSuccessful)
            }
    }

    private fun checkResult(isSuccess: Boolean) {
        if (isSuccess) {
            val intent = Intent(this, MainActivityJ::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show()
            finish()
        }
    }
}