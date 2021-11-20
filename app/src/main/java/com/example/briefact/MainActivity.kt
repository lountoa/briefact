package com.example.briefact

import android.app.PendingIntent.getActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fab_layout.*
import kotlinx.android.synthetic.main.fragment_main.*


class MainActivity : AppCompatActivity() {
    lateinit var show_fab_1: Animation
    lateinit var hide_fab_1: Animation
    lateinit var show_fab_2: Animation
    lateinit var hide_fab_2: Animation
    lateinit var show_fab_3: Animation
    lateinit var hide_fab_3: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)

        var status = false

        show_fab_1 = AnimationUtils.loadAnimation(application, R.anim.fab1_show)
        hide_fab_1 = AnimationUtils.loadAnimation(application, R.anim.fab1_hide)
        show_fab_2 = AnimationUtils.loadAnimation(application, R.anim.fab2_show)
        hide_fab_2 = AnimationUtils.loadAnimation(application, R.anim.fab2_hide)
        show_fab_3 = AnimationUtils.loadAnimation(application, R.anim.fab3_show)
        hide_fab_3 = AnimationUtils.loadAnimation(application, R.anim.fab3_hide)

        fab.setOnClickListener {
            if (status == false) {
                expandFAB()
                status = true
            } else {
                hideFAB()
                status = false
            }
        }

        fab_1.setOnClickListener(View.OnClickListener {

        })

        fab_2.setOnClickListener(View.OnClickListener {

        })

        fab_3.setOnClickListener(View.OnClickListener {

        })
    }

    private fun expandFAB() {
        val layoutParams1: FrameLayout.LayoutParams = fab_1.layoutParams as FrameLayout.LayoutParams
        layoutParams1.rightMargin += (fab_1.width * 1.7).toInt()
        layoutParams1.bottomMargin += (fab_1.height * 0.25).toInt()
        fab_1.layoutParams = layoutParams1
        fab_1.startAnimation(show_fab_1)
        fab_1.isClickable = true

        val layoutParams2 = fab_2.layoutParams as FrameLayout.LayoutParams
        layoutParams2.rightMargin += (fab_2.width * 1.5).toInt()
        layoutParams2.bottomMargin += (fab_2.height * 1.5).toInt()
        fab_2.layoutParams = layoutParams2
        fab_2.startAnimation(show_fab_2)
        fab_2.isClickable = true

        val layoutParams3 = fab_3.layoutParams as FrameLayout.LayoutParams
        layoutParams3.rightMargin += (fab_3.width * 0.25).toInt()
        layoutParams3.bottomMargin += (fab_3.height * 1.7).toInt()
        fab_3.layoutParams = layoutParams3
        fab_3.startAnimation(show_fab_3)
        fab_3.isClickable = true
    }

    private fun hideFAB() {
        val layoutParams1 = fab_1.layoutParams as FrameLayout.LayoutParams
        layoutParams1.rightMargin -= (fab_1.width * 1.7).toInt()
        layoutParams1.bottomMargin -= (fab_1.height * 0.25).toInt()
        fab_1.layoutParams = layoutParams1
        fab_1.startAnimation(hide_fab_1)
        fab_1.isClickable = false

        val layoutParams2 = fab_2.layoutParams as FrameLayout.LayoutParams
        layoutParams2.rightMargin -= (fab_2.width * 1.5).toInt()
        layoutParams2.bottomMargin -= (fab_2.height * 1.5).toInt()
        fab_2.layoutParams = layoutParams2
        fab_2.startAnimation(hide_fab_2)
        fab_2.isClickable = false

        val layoutParams3 = fab_3.layoutParams as FrameLayout.LayoutParams
        layoutParams3.rightMargin -= (fab_3.width * 0.25).toInt()
        layoutParams3.bottomMargin -= (fab_3.height * 1.7).toInt()
        fab_3.layoutParams = layoutParams3
        fab_3.startAnimation(hide_fab_3)
        fab_3.isClickable = false
    }
}