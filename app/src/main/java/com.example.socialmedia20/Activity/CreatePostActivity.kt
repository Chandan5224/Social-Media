package com.example.socialmedia20.Activity

import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.databinding.ActivityCreatePostBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class CreatePostActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreatePostBinding
    private lateinit var postDao: PostDao
    lateinit var imageUri :Uri
    var check=true
    // Create a Cloud Storage reference from the app


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postDao= PostDao()
        requestPermission()

        val getImage=registerForActivityResult(
            ActivityResultContracts.GetContent(), ActivityResultCallback{
            binding.chooseImg.setImageURI(it)
                if (it != null) {
                    imageUri=it
                    check=false
                }
        }
        )

        binding.chooseBtn.setOnClickListener{
            requestPermission()
            getImage.launch("image/*")
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.postBtn.setOnClickListener {
            val input = binding.postInput.text.toString().trim()
            if(input.isNotEmpty()&&!check) {
                val uid=UUID.randomUUID().toString()
                Log.d(ContentValues.TAG, "addPost: $uid")
                val storageRef: StorageReference = FirebaseStorage.getInstance().getReference("images/$uid.jpg")
                storageRef.putFile(imageUri)
                postDao.addPost(input,uid)
                Toast.makeText(this,"Post Added",Toast.LENGTH_SHORT).show()
                check=true
                finish()
            }
        }

    }

    private fun requestPermission() {
        //GuidebyGoogleDevelopers
        if (ContextCompat.checkSelfPermission(
                binding.root.context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }
}