package com.example.briefact.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.example.briefact.R

class ShadowLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        outlineProvider = DynamicCardShadowOutlineProvider(8)
        setBackgroundResource(R.drawable.background_corner_5)
        elevation = dip(8).toFloat()
    }
}
