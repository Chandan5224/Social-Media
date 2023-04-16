package com.example.socialmedia20.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.socialmedia20.Data.News


@Database(entities = [News::class], version = 2)
abstract class NewsDatabase : RoomDatabase(){
    abstract fun newsDao(): NewsDao

    companion object {

        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news__db"
                )
                    .build()

                INSTANCE = instance

                // return instance
                instance
            }
        }
    }

}