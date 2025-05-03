package com.example.newsapp.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
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

@Composable
fun ProfileScreen(
    navController: NavHostController,
    ViewModel: ViewModelProfileScreen,
    themeViewModel: ThemeViewModel
) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: "No Email"
    val bookmarks by ViewModel.readAllData.observeAsState(emptyList())
    val currentTheme by themeViewModel.theme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Info Card (distinct color)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Picture and Email
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profileicon),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        // Buttons and Theme Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val buttonWidth = 130.dp

                            Column {
                                Button(
                                    onClick = { /* Navigate to edit profile */ },
                                    modifier = Modifier
                                        .width(buttonWidth)
                                        .padding(bottom = 8.dp)
                                ) {
                                    Text("Edit Profile", fontSize = 14.sp)
                                }

                                Button(
                                    onClick = {
                                        auth.signOut()
                                        navController.navigate("main_screen") {
                                            popUpTo("profile_screen") { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.width(buttonWidth)
                                ) {
                                    Text("Logout", fontSize = 14.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { themeViewModel.toggleTheme() },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .border(1.dp, Color.Gray, shape = CircleShape)
                                    .size(50.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentTheme == "Dark") Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Toggle Theme",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "My Bookmarks",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bookmarks List
            if (bookmarks.isEmpty()) {
                item {
                    Text(
                        text = "No bookmarks yet.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                itemsIndexed(bookmarks) { _, bookmark ->
                    BookmarkCard(bookmark = bookmark, viewModel = ViewModel)
                }
            }
        }
    }
}

@Composable
fun BookmarkCard(bookmark: BookmarkEntity, viewModel: ViewModelProfileScreen) {
    Log.d("BookmarkCard", "Bookmark: $bookmark")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),

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
                    text = bookmark.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
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
