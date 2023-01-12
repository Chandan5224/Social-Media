package com.example.socialmedia20.Data

import android.net.Uri
import java.net.URI

data class Post(
        val text: String = "",
        val createdBy: User = User(),
        val createdAt: Long = 0L,
        val imageUrl: String= "",
        val likedBy: ArrayList<String> = ArrayList())