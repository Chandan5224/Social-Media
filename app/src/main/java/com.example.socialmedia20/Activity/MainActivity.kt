package com.example.socialmedia20.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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

        // set custom toolbar
        binding.toolbar.title=""
        setSupportActionBar(binding.toolbar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_account){
            Toast.makeText(this, "Click Account Icon.", Toast.LENGTH_SHORT).show();
        }
        if (item.itemId== R.id.nav_post){
            val intent= Intent(this,CreatePostActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


}
