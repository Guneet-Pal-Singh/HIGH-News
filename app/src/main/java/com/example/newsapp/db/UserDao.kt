package com.example.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    // Insert a new user into the database
    @Insert
    suspend fun insertUser(user: UserEntity)

    // Get a user by their email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Insert a new bookmark
    @Insert
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    // Get all bookmarks
    @Query("SELECT * FROM bookmarks")
    fun getAllBookmarks(): LiveData<List<BookmarkEntity>>

    // Delete a specific bookmark by its object
    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    suspend fun getBookmarkByUrl(url: String): BookmarkEntity?

}
