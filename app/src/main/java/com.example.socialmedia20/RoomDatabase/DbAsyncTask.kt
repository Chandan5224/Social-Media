package com.example.socialmedia20.RoomDatabase

import android.content.Context
import android.os.AsyncTask
import com.example.socialmedia20.Data.News

class DbAsyncTask(val context: Context, val news: News, val mode:Int): AsyncTask<Void, Void, Boolean>(){
    val db=NewsDatabase.getDatabase(context)
    override fun doInBackground(vararg params: Void?): Boolean {
        when (mode){
            1 ->{
                //check is save or not
                val news: News?= db.newsDao().getById(news.title)
                return news!=null
            }
            2 ->{
                // insert data
                db.newsDao().addNews(news)
                return true
            }
            3->{
                //  delete
                db.newsDao().deleteNews(news)
                return true
            }
        }


        return false
    }

}