package com.example.newsapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.newsapp.api.Article
import com.example.newsapp.screens.*
import com.example.newsapp.ui.theme.NewsAppTheme
import com.google.android.gms.location.*
import com.google.gson.Gson
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var homeScreenViewModel: ViewModelHomeScreen? = null
    private lateinit var themeViewModel: ThemeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize themeViewModel once
        themeViewModel = ThemeViewModel(application)
        themeViewModel.registerPowerSaverReceiver()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                if (!isLocationEnabled()) {
                    setupUIWithViewModel("us")
                } else {
                    getLastLocation { countryCode ->
                        setupUIWithViewModel(countryCode)
                    }
                }
            } else {
                Log.w("Location", "Permission denied")
                setupUIWithViewModel("us") // fallback default
            }
        }

        val fineGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else if (!isLocationEnabled()) {
            // Location services are OFF
            setupUIWithViewModel("us")
        } else {
            getLastLocation { countryCode ->
                setupUIWithViewModel(countryCode)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun setupUIWithViewModel(countryCode: String) {
        val factory = ViewModelHomeScreenFactory(countryCode)
        homeScreenViewModel = ViewModelProvider(this, factory).get(ViewModelHomeScreen::class.java)

        setContent {
            val theme by themeViewModel.theme.collectAsState()
            val navController = rememberNavController()
            val viewModel = ViewModelProfileScreen(application)

            NewsAppTheme(theme = theme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash_screen") { SplashScreen(navController) }
                        composable("main_screen") { LoginScreen(navController) }
                        composable("home_screen") {
                            HomeScreen(navController, viewModel = homeScreenViewModel!!)
                        }
                        composable("register_screen") { RegisterScreen(navController) }
                        composable("profile_screen") {
                            ProfileScreen(navController, viewModel, themeViewModel)
                        }
                        composable("news_detail/{article}") { backStackEntry ->
                            val json = backStackEntry.arguments?.getString("article")
                            val article = Gson().fromJson(json, Article::class.java)
                            NewsDetailScreen(navController, article, viewModel)
                        }
                        composable(
                            route = "articleDetail/{title}/{imageUrl}/{description}/{url}",
                            arguments = listOf(
                                navArgument("title") { type = NavType.StringType },
                                navArgument("imageUrl") { type = NavType.StringType },
                                navArgument("description") { type = NavType.StringType },
                                navArgument("url") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val title = backStackEntry.arguments?.getString("title") ?: ""
                            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                            val description = backStackEntry.arguments?.getString("description") ?: ""
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            ArticleDetailScreen(
                                title = title,
                                imageUrl = imageUrl,
                                description = description,
                                url = url,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getLastLocation(callback: (String) -> Unit) {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1000
            numUpdates = 1
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val countryCode = addresses?.firstOrNull()?.countryCode?.lowercase(Locale.ROOT) ?: "us"
                        Log.e("Location", "$countryCode")
                        callback(countryCode)
                    } catch (e: Exception) {
                        Log.e("Location", "Geocoder failed", e)
                        callback("us")
                    }
                } else {
                    callback("us")
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }
}
