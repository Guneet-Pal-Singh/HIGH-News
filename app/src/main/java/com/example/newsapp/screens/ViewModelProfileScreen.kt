package com.example.newsapp.screens

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.db.BookmarkEntity
import com.example.newsapp.db.Repository
import com.example.newsapp.db.UserDatabase
import kotlinx.coroutines.launch

class ViewModelProfileScreen(application: Application) : AndroidViewModel(application){
    val readAllData: LiveData<List<BookmarkEntity>>
    private val repository:Repository

    init {
        var userDao= UserDatabase.getDatabase(application).userDao().also {
            Log.d("UserViewModel", "readAllData initialized")
        }
        repository=Repository(userDao)
        readAllData=repository.readAllData
    }

    fun insert(bookmark: BookmarkEntity){
        viewModelScope.launch{
            repository.insertBookmark(bookmark)
        }
    }

    fun delete(bookmark: BookmarkEntity){
        viewModelScope.launch{
            repository.deleteBookmark(bookmark)
        }
    }

    fun getBookMarks(): LiveData<List<BookmarkEntity>> {
        return readAllData
    }
}