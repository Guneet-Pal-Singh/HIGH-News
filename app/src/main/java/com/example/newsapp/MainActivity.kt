package com.example.newsapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.api.Article
import com.example.newsapp.screens.*
import com.example.newsapp.ui.theme.NewsAppTheme
import com.google.android.gms.location.*
import com.google.gson.Gson
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val themeViewModel = ThemeViewModel(application)

        // Register permission launcher
        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (fineGranted || coarseGranted) {
                getLastLocation()
            } else {
                Log.w("Location", "Permission denied")
            }
        }

        setContent {
            val theme by themeViewModel.theme.collectAsState()
            themeViewModel.registerPowerSaverReceiver()
            val navController = rememberNavController()
            val viewModel = ViewModelProfileScreen(application)

            // Request permissions
            LaunchedEffect(Unit) {
                val fineGranted = ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val coarseGranted = ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!fineGranted && !coarseGranted) {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    getLastLocation()
                }
            }

            NewsAppTheme(theme = theme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash_screen") { SplashScreen(navController) }
                        composable("main_screen") { LoginScreen(navController) }
                        composable("home_screen") { HomeScreen(navController) }
                        composable("register_screen") { RegisterScreen(navController) }
                        composable("profile_screen") {
                            ProfileScreen(navController, viewModel, themeViewModel)
                        }
                        composable("news_detail/{article}") { backStackEntry ->
                            val json = backStackEntry.arguments?.getString("article")
                            val article = Gson().fromJson(json, Article::class.java)
                            NewsDetailScreen(navController, article, viewModel)
                        }
                    }
                }
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getLastLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1000
            numUpdates = 1
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude
                    Log.d("Location", "Lat: $lat, Lng: $lng")

                    // Get country using Geocoder
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(lat, lng, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val country = addresses[0].countryName
                            Log.d("Location", "Country: $country")
                        } else {
                            Log.d("Location", "No address found")
                        }
                    } catch (e: Exception) {
                        Log.e("Location", "Geocoder failed", e)
                    }
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }
}
