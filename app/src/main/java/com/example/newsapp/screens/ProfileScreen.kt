package com.example.newsapp.screens

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.newsapp.R
import com.example.newsapp.db.BookmarkEntity
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit

@Composable
fun ProfileScreen(navController: NavHostController, ViewModel: ViewModelProfileScreen) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email
    var userName by remember { mutableStateOf("") }
    val bookmarks by ViewModel.readAllData.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Image(
            painter = painterResource(id = R.drawable.profileicon),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        // User Info
        Text(text = userName, style = MaterialTheme.typography.headlineMedium)
        Text(text = userEmail ?: "No Email", color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        // Edit Profile Button
        Button(
            onClick = { /* Navigate to Edit Profile Screen */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Profile" , fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

// Logout Button
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("main_screen") {
                    popUpTo("profile_screen") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(
                "Logout",
                fontSize = 18.sp
            )
        }


        Spacer(modifier = Modifier.height(8.dp))

        // Bookmarks Header
        Text(
            text = "My Bookmarks",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bookmarks List
        BookmarkList(
            bookmarks = bookmarks,
            viewModel = ViewModel
        )

        // Theme Selection
        Theme()
    }
}

@Composable
fun BookmarkList(
    bookmarks: List<BookmarkEntity>,
    viewModel: ViewModelProfileScreen
) {
    Log.d("BookmarkList", "Bookmarks: $bookmarks")
    if (bookmarks.isEmpty()) {
        Text(
            text = "No bookmarks yet.",
            color = Color.Gray,
            modifier = Modifier.padding(8.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(bookmarks) { index, bookmark ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        val imageModifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))

                        if (bookmark.imageURL == "empty") {
                            Image(
                                painter = painterResource(id = R.drawable.placeholder_image),
                                contentDescription = "Bookmark Image",
                                modifier = imageModifier
                            )
                        } else {
                            AsyncImage(
                                model = bookmark.imageURL,
                                contentDescription = "Bookmark Image",
                                modifier = imageModifier,
                                placeholder = painterResource(id = R.drawable.placeholder_image),
                                error = painterResource(id = R.drawable.error_image)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${bookmark.title}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                maxLines = 5,
                                overflow = TextOverflow.Clip,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { viewModel.delete(bookmark) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete Bookmark",
                                    tint = Color(0xFFEF5350),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Theme(){
    Column(modifier = Modifier.padding(5.dp)) {
        Button(onClick = {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }) {
            Text(text = "Light Theme")
        }

        Button(onClick = {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }) {
            Text(text = "Dark Theme")
        }

        Button(onClick = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }) {
            Text(text = "System Default")
        }
    }
}

fun saveThemePreference(context: Context, mode: String) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit() { putString("theme_mode", mode) }
}

fun loadThemePreference(context: Context): String {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return prefs.getString("theme_mode", "system") ?: "system"
}