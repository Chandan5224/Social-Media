package com.example.socialmedia20.Activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.socialmedia20.Adapters.ScreenSlidePagerAdapter
import com.example.socialmedia20.Fragments.FirstScreen
import com.example.socialmedia20.Fragments.SecondScreen
import com.example.socialmedia20.Fragments.ThirdScreen
import com.example.socialmedia20.R
import com.example.socialmedia20.databinding.ActivityIntroBinding
import kotlin.math.log

class IntroActivity : AppCompatActivity() {

    lateinit var binding: ActivityIntroBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentList = arrayListOf(
            FirstScreen(), SecondScreen(), ThirdScreen()
        )
        val adapter = ScreenSlidePagerAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )
        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        val logIn = sharedPreferences.getBoolean("logIn", false)

        if (logIn) {
            Handler().postDelayed({
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }, 80)
        } else {
            binding.viewPager.adapter = adapter
        }

    }
}