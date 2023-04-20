package com.example.socialmedia20.RoomDatabase

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.socialmedia20.Data.DataConverter
import com.example.socialmedia20.Data.Post
import com.example.socialmedia20.Data.PostDao


@Database(entities = [Post::class], version = 1)
@TypeConverters(DataConverter::class)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao.PostDaoRoomDB

    companion object {
        @Volatile
        private var INSTANCE: PostDatabase? = null

        fun getDatabase(context: Context): PostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PostDatabase::class.java,
                    "post__db"
                )
                    .build()
                INSTANCE = instance

                // return instance
                instance
            }
        }
    }
}