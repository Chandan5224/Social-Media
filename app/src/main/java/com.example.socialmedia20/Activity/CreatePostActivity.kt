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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.R
import com.example.socialmedia20.databinding.ActivityCreatePostBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class CreatePostActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreatePostBinding
    private lateinit var postDao: PostDao
    private lateinit var storageRef :StorageReference
    lateinit var imageUri :Uri
    private var imageUrl: String =""


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
            onBackPressed()
        }

        binding.postBtn.setOnClickListener {
            val input = binding.postInput.text.toString().trim()
            if(input.isNotEmpty()&&!check) {
                storageRef=FirebaseStorage.getInstance().reference.child("images")
                storageRef=storageRef.child(System.currentTimeMillis().toString())
                storageRef.putFile(imageUri).addOnCompleteListener{
                    if(it.isSuccessful){
                        storageRef.downloadUrl.addOnSuccessListener {uri ->
                            imageUrl=uri.toString()
                            postDao.addPost(input,imageUrl)
                        }
                    }else{
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
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

    override fun onBackPressed() {

        if(binding.postInput.text.isNotEmpty()) {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.dialogTitle)
            //set message for alert dialog
            builder.setMessage(R.string.dialogMessage)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                finish()
            }
            //performing cancel action
            builder.setNeutralButton("Cancel") { dialogInterface, which ->

            }
            //performing negative action
//            builder.setNegativeButton("No"){dialogInterface, which ->
//                Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
//            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }else
        {
            finish()
        }
    }
}