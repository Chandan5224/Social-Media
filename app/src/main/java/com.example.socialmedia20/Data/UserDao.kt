package com.example.socialmedia20.Data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    private val db=FirebaseFirestore.getInstance()
    private val userCollection=db.collection("users")
    val auth = Firebase.auth
    fun addUser(user: User?)
    {
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }
        }
    }


    fun getUserByID(uId:String) : Task<DocumentSnapshot> {
        return userCollection.document(uId).get()
    }
}