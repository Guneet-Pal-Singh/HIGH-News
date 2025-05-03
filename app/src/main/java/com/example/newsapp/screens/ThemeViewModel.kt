package com.example.newsapp.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ThemeViewModel:ViewModel() {
    private val _theme= MutableStateFlow<String>("System")
    val theme= _theme

    fun setTheme(theme: String) {
        _theme.value = theme
    }

    fun getTheme(): String {
        return _theme.value
    }

    fun toggleTheme() {
        _theme.value = if (_theme.value == "System") "Light" else "System"
    }
}