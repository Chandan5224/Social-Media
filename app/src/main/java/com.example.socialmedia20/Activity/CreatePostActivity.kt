package com.example.socialmedia20.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.R
import com.example.socialmedia20.databinding.ActivityCreatePostBinding
import com.example.socialmedia20.databinding.ActivitySignInBinding

class CreatePostActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreatePostBinding
    private lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postDao= PostDao()

        binding.postBtn.setOnClickListener {
            val input = binding.postInput.text.toString().trim()
            if(input.isNotEmpty()) {
                postDao.addPost(input)
                Toast.makeText(this,"Post Added",Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }
}