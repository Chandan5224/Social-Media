//package com.example.socialmedia20.RoomDatabase
//
//import androidx.lifecycle.LiveData
//import com.example.socialmedia20.Data.News
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class NewsRepository(private val newsDao: NewsDao) {
//    val getAll: LiveData<List<News>> = newsDao.getAll()
//
//    suspend fun addNews(news: News){
//        newsDao.addNews(news)
//    }
//
//    suspend fun deleteAll(){
//        newsDao.deleteAll()
//    }
//
//    suspend fun deleteNews(news: News){
//        newsDao.deleteNews(news)
//    }
//    fun getById(newsTitle:String): LiveData<News> {
//        return newsDao.getById(newsTitle)
//    }
//
//
//}