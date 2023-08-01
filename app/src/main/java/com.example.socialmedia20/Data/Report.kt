package com.example.socialmedia20.Data

data class Report(
    val reportUid: String,
    val reportBy: String,
    val userName: String,
    val userEmailId: String,
    val msg: String,
    val post: Post
)
