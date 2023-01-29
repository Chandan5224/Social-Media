package com.example.socialmedia20.Data

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    val auth = Firebase.auth
    fun addUser(user: User?) {
        user?.let {us->
            userCollection.document(user.uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document != null) {
                        if (document.exists()) {
                            Log.d("TAG", "Document already exists.")
                        } else {
                            Log.d("TAG", "Document doesn't exist.")
                            userCollection.document(user.uid).set(us)
                        }
                    }
                    Log.d("Name",document.get("displayName").toString())
                }
            }
        }
    }

    fun updateUser(user: User?)
    {
        user?.let {
            userCollection.document(user.uid).set(it)
        }
    }

    fun getPosts(userId:String):ArrayList<String>{
        var posts:ArrayList<String> = ArrayList()
        GlobalScope.launch {
            val user=UserDao().getUserByID(userId).await().toObject(User::class.java)
            val pList=user!!.post
            posts=pList
        }
        return posts
    }




    fun getUserByID(uId: String): Task<DocumentSnapshot> {
        return userCollection.document(uId).get()
    }
}