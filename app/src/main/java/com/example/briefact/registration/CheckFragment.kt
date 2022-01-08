package com.example.briefact.registration

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.briefact.R

class CheckFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(onBoardingFinished()){
            findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_splash, container, false)
    }

    private fun onBoardingFinished(): Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

}