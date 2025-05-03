package com.example.newsapp.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ThemeViewModel(application: Application):AndroidViewModel(application) {
    private val _theme= MutableStateFlow<String>("System")
    val theme= _theme
    private val context=application.applicationContext

    private var receiverRegistered = false

    fun registerPowerSaverReceiver() {
        if (receiverRegistered) return

        val filter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        context.registerReceiver(powerSaverReceiver, filter)
        receiverRegistered = true
    }

    private val powerSaverReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                val isOn = isPowerSaverOn()
                Log.d("ThemeViewModel", "Power Saver changed: $isOn")
                _theme.value = if (isOn) "Dark" else "Light"
            }
        }
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

    fun isPowerSaverOn(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode
    }

    override fun onCleared() {
        super.onCleared()
        try {
            context.unregisterReceiver(powerSaverReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver already unregistered or never registered
        }
    }
}