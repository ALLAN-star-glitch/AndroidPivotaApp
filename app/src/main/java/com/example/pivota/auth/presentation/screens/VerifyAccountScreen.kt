package com.example.pivota.auth.presentation.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R

// Color
val InfoBorder = Color(0xFFE6B800)

@Preview
@Composable
fun VerifyAccountScreen() {
    // State to handle multiple documents
    val uploadedFiles = remember {
        mutableStateListOf(
            SelectedDocument("1", "business_registration.pdf", "2.4 MB")
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Verify your account", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Skip", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = "Uploading verification documents helps build trust and access premium features in the future.",
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = "You can complete this later in your account settings.",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 12.sp
                )
            }

            // 2. Info Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InfoBorder.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .drawBehind {
                            // Yellow left border highlight
                            drawRect(color = InfoBorder, size = size.copy(width = 4.dp.toPx()))
                        }
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Accepted formats: PDF, JPG, PNG. Max file size: 5 MB.",
                        fontSize = 14.sp,
                    )
                }
            }

            // 3. Upload Trigger Box
            item {
                Text("Upload documents", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                UploadBox(onFileClicked = {
                    // Logic to launch file picker would go here
                })
            }

            // 4. List of Uploaded Documents
            items(uploadedFiles) { file ->
                DocumentItem(
                    file = file,
                    onRemove = { uploadedFiles.remove(file) }
                )
            }

            // 5. Action Buttons
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Handle Upload */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    Text("Upload & Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Text(
                    text = "Skip for now",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clickable { /* Skip */ },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun UploadBox(onFileClicked: () -> Unit) {
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .drawBehind {
                drawRoundRect(color = Color.LightGray, style = stroke)
            }
            .clickable { onFileClicked() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cloud_upload_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Tap to upload file", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("or drag and drop here", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun DocumentItem(file: SelectedDocument, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File Icon
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.docs_24px),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // File Details
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${file.size}  •  ", color = Color.Gray, fontSize = 12.sp)
                    Text(file.status, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    Icon(
                        Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp).padding(start = 2.dp)
                    )
                }
            }

            // Remove Button
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray)
            }
        }
    }
}