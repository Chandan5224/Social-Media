package com.example.socialmedia20.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.socialmedia20.Data.DataConverter
import com.example.socialmedia20.Data.News


@Database(entities = [News::class], version = 3 , exportSchema = false)
@TypeConverters(DataConverter::class)
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
                    .addMigrations(MIGRATION_2_3)
                    .build()

                INSTANCE = instance

                // return instance
                instance
            }
        }
    }
}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE 'news' ADD COLUMN 'saveTime' STRING NOT NULL DEFAULT ''")
    }
}
