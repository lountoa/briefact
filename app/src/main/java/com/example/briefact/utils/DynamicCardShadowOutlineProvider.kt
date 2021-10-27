package com.example.briefact.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider


class DynamicCardShadowOutlineProvider(private val topMargin: Int = 0) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val shadowLeftOffset = 0
        val shadowTopOffset = 0
        val shadowRightOffset = 0
        val elevation = 8
        val shadowCornerRadius = 5

        outline.setRoundRect(
            view.dip(shadowLeftOffset),
            view.dip(shadowTopOffset) + view.dip(topMargin),
            view.width - view.dip(shadowRightOffset),
            view.height - view.dip(elevation) + view.dip(topMargin),
            view.dip(shadowCornerRadius).toFloat())
    }

//    private fun View.dip(value: Float): Int = (value * (resources?.displayMetrics?.density
//            ?: 0f)).toInt()
}

fun View.dip(value: Int): Int = (value * (resources?.displayMetrics?.density
    ?: 0f)).toInt()
