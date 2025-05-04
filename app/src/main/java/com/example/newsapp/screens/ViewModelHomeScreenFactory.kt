package com.example.newsapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelHomeScreenFactory(private val location: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelHomeScreen::class.java)) {
            return ViewModelHomeScreen(location) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}