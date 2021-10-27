package com.example.briefact.registration.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.briefact.R
import com.example.briefact.registration.onboarding.screens.FirstScreen
import com.example.briefact.registration.onboarding.screens.SecondScreen
import com.example.briefact.registration.onboarding.screens.ThirdScreen
import kotlinx.android.synthetic.main.fragment_view_pager.view.*

class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            fm = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle
        )

        view.viewPager.adapter = adapter
        view.viewPager.setPageTransformer(MarginPageTransformer(1500))

        return view
    }

}