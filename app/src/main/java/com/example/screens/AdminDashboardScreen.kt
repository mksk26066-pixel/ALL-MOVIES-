package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.auth.AuthManager
import com.example.data.AppDatabase
import com.example.data.VideoEntity
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { AuthManager.getInstance(context) }
    val videoDao = remember { AppDatabase.getDatabase(context).videoDao() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var thumbnailUrl by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Admin Dashboard", color = TextPrimary) },
            actions = {
                TextButton(onClick = { authManager.logout() }) {
                    Text("Logout", color = AccentEmerald)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Upload Video",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminTextField(value = title, onValueChange = { title = it }, label = "Video Title")
            Spacer(modifier = Modifier.height(12.dp))
            AdminTextField(value = description, onValueChange = { description = it }, label = "Description")
            Spacer(modifier = Modifier.height(12.dp))
            AdminTextField(value = videoUrl, onValueChange = { videoUrl = it }, label = "Video Source URL (.mp4)")
            Spacer(modifier = Modifier.height(12.dp))
            AdminTextField(value = thumbnailUrl, onValueChange = { thumbnailUrl = it }, label = "Thumbnail URL (.jpg/.png)")
            Spacer(modifier = Modifier.height(12.dp))
            AdminTextField(value = duration, onValueChange = { duration = it }, label = "Duration (e.g. 10:45)")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isBlank() || videoUrl.isBlank()) {
                        errorMessage = "Title and Video URL are required."
                        successMessage = null
                        return@Button
                    }
                    scope.launch {
                        try {
                            videoDao.insertVideo(
                                VideoEntity(
                                    title = title,
                                    description = description,
                                    filePath = videoUrl,
                                    thumbnailPath = thumbnailUrl,
                                    duration = duration,
                                    views = 0,
                                    uploadDate = java.lang.System.currentTimeMillis()
                                )
                            )
                            successMessage = "Video uploaded successfully!"
                            errorMessage = null
                            title = ""
                            description = ""
                            videoUrl = ""
                            thumbnailUrl = ""
                            duration = ""
                        } catch (e: Exception) {
                            errorMessage = "Failed to upload video: ${e.message}"
                            successMessage = null
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Text("Upload Video", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (successMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(successMessage!!, color = AccentEmerald, style = MaterialTheme.typography.bodyMedium)
            }
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun AdminTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceDarkGlass,
            unfocusedContainerColor = SurfaceDarkGlass,
            focusedBorderColor = PrimaryIndigo,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
        )
    )
}
