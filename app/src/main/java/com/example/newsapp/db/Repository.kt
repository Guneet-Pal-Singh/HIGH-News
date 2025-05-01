package com.example.newsapp.db

import androidx.lifecycle.LiveData

class Repository (private val userDao: UserDao) {
    val readAllData: LiveData<List<BookmarkEntity>> = userDao.getAllBookmarks()

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun insertBookmark(bookmark: BookmarkEntity) {
        userDao.insertBookmark(bookmark)
    }

    suspend fun deleteBookmark(bookmark: BookmarkEntity) {
        userDao.deleteBookmark(bookmark)
    }

    suspend fun isBookmarkExists(url: String): Boolean {
        return userDao.getBookmarkByUrl(url) != null
    }
}