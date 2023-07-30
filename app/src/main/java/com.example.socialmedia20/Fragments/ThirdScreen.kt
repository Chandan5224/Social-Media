package com.example.socialmedia20.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmedia20.Activity.SignInActivity
import com.example.socialmedia20.R
import com.example.socialmedia20.databinding.FragmentFirstScreenBinding
import com.example.socialmedia20.databinding.FragmentThirdScreenBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdScreen.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdScreen : Fragment() {
    lateinit var binding: FragmentThirdScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdScreenBinding.inflate(layoutInflater, container, false)

        binding.btnFinish.setOnClickListener {
            sharedPreferences.edit().putBoolean("logIn", true).apply()
            val intent = Intent(binding.root.context, SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return binding.root
    }

}