package com.example.socialmedia20.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class News(
//    @PrimaryKey(autoGenerate = true) val newsId:Int,
    @PrimaryKey val title: String,
    @ColumnInfo(name = "author") val author : String,
    @ColumnInfo(name = "url") val url : String,
    @ColumnInfo(name = "imageUrl") val imageUrl: String,
    @ColumnInfo(name = "time") val time: String
)