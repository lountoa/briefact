package com.example.briefact

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.briefact.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.concurrent.schedule
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER

import android.widget.TextView

private const val HIDE_KEYBOARD_DELAY = 250L
private const val SHOW_KEYBOARD_DELAY = 250L

fun Fragment.hideKeyboard() {
    Timer("Keyboard", false)
        .schedule(HIDE_KEYBOARD_DELAY) {
            val inputManager: InputMethodManager =
                (activity as AppCompatActivity)
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
}

fun Fragment.showKeyboard() {
    Timer("Keyboard", false)
        .schedule(SHOW_KEYBOARD_DELAY) {
            val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
}