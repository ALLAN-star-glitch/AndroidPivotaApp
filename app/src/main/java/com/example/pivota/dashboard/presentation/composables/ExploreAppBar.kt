package com.example.pivota.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R

@Composable
fun HomeAppBar(
    userName: String = "Alexar",
    isGuest: Boolean = false,
    onLockedAction: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onUpgradeClick: () -> Unit = {},
    onLearnMoreClick: () -> Unit = {}
) {
    val tealBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF008080).copy(alpha = 0.8f),
            Color(0xFF008080).copy(alpha = 0.6f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(tealBackground)
            .padding(top = 16.dp)
    ) {
        // Optional background image
        Image(
            painter = painterResource(id = R.drawable.happy_clients),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.1f),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Menu, SmartMatch Badge, and Notifications
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }

                // SmartMatch Badge at center-right
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "SmartMatch™",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row {
                    IconButton(
                        onClick = {
                            if (isGuest) onLockedAction() else onMessagesClick()
                        }
                    ) {
                        Icon(Icons.Default.Email, contentDescription = "Messages", tint = Color.White)
                    }
                    IconButton(
                        onClick = {
                            if (isGuest) onLockedAction() else onNotificationsClick()
                        }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Greeting + Avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.avatarz),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .size(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Hi $userName", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Get started by exploring jobs, services, and houses",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Call-to-Action Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Unlock more features",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Text("✨", fontSize = 24.sp)
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Upgrade your account to premium and post unlimited listings.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )

                        Spacer(Modifier.height(12.dp))

                        Row {
                            Button(
                                onClick = {
                                    if (isGuest) onLockedAction() else onUpgradeClick()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text("Upgrade", color = Color.Black)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = onLearnMoreClick,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Learn More")
                            }
                        }
                    }
                }
            }
        }
    }
}
