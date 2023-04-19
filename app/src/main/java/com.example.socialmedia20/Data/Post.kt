package com.example.socialmedia20.Data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URI

@Entity(tableName = "posts")
data class Post(
        @PrimaryKey val uid:String="",
        @ColumnInfo(name = "text") val text: String = "",
        @ColumnInfo(name = "createdBy") val createdBy: User = User(),
        @ColumnInfo(name = "createdAt") val createdAt: Long = 0L,
        @ColumnInfo(name = "imageUrl") val imageUrl: String= "",
        @ColumnInfo(name = "likedBy") val likedBy: ArrayList<String> = ArrayList()
)