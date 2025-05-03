package com.example.newsapp.screens

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ThemeViewModel(application: Application):AndroidViewModel(application) {
    private val _theme= MutableStateFlow<String>("System")
    val theme= _theme
    private val context=application.applicationContext

    init{
        checkBatteryLevel()
    }

    fun setTheme(theme: String) {
        _theme.value = theme
    }

    fun getTheme(): String {
        return _theme.value
    }

    fun toggleTheme() {
        if (_theme.value == "System" || _theme.value == "Dark")
            _theme.value="Light"
        else
            _theme.value="Dark"
    }

    fun checkBatteryLevel() {
        val batteryIntent = context.applicationContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        if (level >= 0 && scale > 0) {
            val batteryPct = (level * 100) / scale
            if (batteryPct <= 15) {
                _theme.value = "Dark"
            }
        }
    }
}