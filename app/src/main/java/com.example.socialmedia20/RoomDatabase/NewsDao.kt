package com.example.socialmedia20.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.socialmedia20.Data.News


@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun addNews(news: News)

    @Delete
     fun deleteNews(news: News)

    @Query("SELECT * FROM news ORDER BY saveTime DESC")
    fun getAll(): List<News>

    @Query("DELETE FROM news")
    fun deleteAll()

    @Query("SELECT * FROM news WHERE title=:newsTitle")
    fun getById(newsTitle :String): News
}