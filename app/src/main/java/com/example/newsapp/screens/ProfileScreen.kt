package com.example.newsapp.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.newsapp.R
import com.example.newsapp.db.BookmarkEntity
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavHostController,ViewModel: ViewModelProfileScreen) {
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

        Spacer(modifier = Modifier.height(16.dp))

        // User Info
        Text(text = userName, style = MaterialTheme.typography.headlineMedium)
        Text(text = userEmail ?: "No Email", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Profile Button
        Button(
            onClick = { /* Navigate to Edit Profile Screen */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Profile")
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
            Text("Logout", color = MaterialTheme.colorScheme.onError)
        }

        Spacer(modifier = Modifier.height(24.dp))

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
            viewModel=ViewModel
        )
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
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {

                        if (bookmark.imageURL=="empty") {
                            Image(
                                painter = painterResource(id = R.drawable.placeholder_image),
                                contentDescription = "Bookmark Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        } else {
                            AsyncImage(
                                model = bookmark.imageURL,
                                contentDescription = "Bookmark Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                placeholder = painterResource(id = R.drawable.placeholder_image),
                                error= painterResource(id = R.drawable.error_image)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}. ${bookmark.title}",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = {
                                    viewModel.delete(bookmark)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete Bookmark",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}