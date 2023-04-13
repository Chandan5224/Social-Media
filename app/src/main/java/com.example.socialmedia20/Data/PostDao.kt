package com.example.socialmedia20.Data

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.math.log

class PostDao {
    private val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("posts")
    private val userCollection = db.collection("users")
    val auth = Firebase.auth

    fun addPost(text: String, imageUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            // get current user
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserByID(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val postId = getRandomString(15)
            user.post.add(postId)
            val post = Post(postId, text, user, currentTime, imageUrl)
            postCollection.document(post.uid).set(post).addOnSuccessListener {

            } /// set new post
            userDao.updateUser(user)
        }
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun updatePost(text: String, imageUrl: String, postId: String) {
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserByID(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val post = getPostByID(postId).await().toObject(Post::class.java)!!
            val nPost = Post(post.uid, text, user, currentTime, imageUrl, post.likedBy)
            postCollection.document(postId).set(nPost)
        }
    }

    fun getPostByID(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }

    fun updateLikes(postId: String) {
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val post = getPostByID(postId).await().toObject(Post::class.java)!!
            val isLiked = post.likedBy.contains(currentUserId)

            if (isLiked) {
                post.likedBy.remove(currentUserId)
            } else {
                post.likedBy.add(currentUserId)
            }
            postCollection.document(postId).set(post) // for update post
        }

    }

    fun updateSave(post: Post) {
        GlobalScope.launch {
            // get current user
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserByID(currentUserId).await().toObject(User::class.java)!!
            val isSave = user.save.contains(post.uid)
            if (!isSave)
                user.save.add(post.uid)
            else
                user.save.remove(post.uid)
            userDao.updateUser(user)
        }
    }

    fun deletePost(postId: String) {
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserByID(currentUserId).await().toObject(User::class.java)!!
            user.post.remove(postId)
            userCollection.document(user.uid).set(user)
            postCollection.document(postId).delete()
        }
    }
}