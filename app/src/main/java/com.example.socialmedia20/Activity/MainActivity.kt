package com.example.socialmedia20.Activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmedia20.*
import com.example.socialmedia20.Adapters.VPAdapter
import com.example.socialmedia20.Fragments.Home
import com.example.socialmedia20.Fragments.Memes
import com.example.socialmedia20.Fragments.News
import com.example.socialmedia20.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Call
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        val vpAdapter= VPAdapter(supportFragmentManager)
        vpAdapter.apply {
            add(Home(),"")
            add(Memes(),"")
            add(News(),"")
        }
        binding.viewPager.adapter=vpAdapter

        binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_baseline_home_24)
        binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_memes)
        binding.tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_news)
        binding.tabLayout.tabGravity=TabLayout.GRAVITY_FILL
    }

    private fun statusBarColor()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            window.statusBarColor=resources.getColor(R.color.white,this.theme)
        else window.statusBarColor=resources.getColor(R.color.teal_200)
    }
}
