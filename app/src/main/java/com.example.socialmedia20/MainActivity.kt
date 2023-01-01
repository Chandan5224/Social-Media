package com.example.socialmedia20

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.inflate
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.core.view.get
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.recycleview.Fruit
import com.example.recycleview.FruitAdapter
import com.example.socialmedia20.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Call
        setupViewPager()


    }

    private fun setupViewPager() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        val vpAdapter=VPAdapter(supportFragmentManager)
        vpAdapter.apply {
            add(Trending(),"Trending")
            add(Memes(),"Memes")
            add(News(),"News")
        }
        binding.viewPager.adapter=vpAdapter

        binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_trending)
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