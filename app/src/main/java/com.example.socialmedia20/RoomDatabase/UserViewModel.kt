//package com.example.socialmedia20.RoomDatabase
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.viewModelScope
//import com.example.socialmedia20.Data.News
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class UserViewModel(application: Application): AndroidViewModel(application) {
//
//    val getAllData: LiveData<List<News>>
//    private val repository: NewsRepository
//
//
//    init {
//        val newsDao = NewsDatabase.getDatabase(application).newsDao()
//        repository = NewsRepository(newsDao)
//        getAllData = repository.getAll
//    }
//
//    fun addNews(news: News) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.addNews(news)
//        }
//    }
//
//    fun deleteAll() {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteAll()
//        }
//    }
//
//    fun delete(news: News){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteNews(news)
//        }
//    }
//
//    fun getById(newsTitle: String): LiveData<News> {
//        return repository.getById(newsTitle)
//    }
//
//
//
//}