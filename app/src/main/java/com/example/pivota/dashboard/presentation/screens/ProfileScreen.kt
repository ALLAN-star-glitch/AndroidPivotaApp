package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R

@Composable
fun ProfileScreen() {
    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = padding.calculateBottomPadding() + 24.dp)
        ) {
            // 🔝 Unified Hero Header
            ProfileHeroHeader(primaryTeal, goldenAccent)

            Spacer(modifier = Modifier.height(32.dp))

            // 👤 1. Account Section
            ProfileSection(title = "Account Management") {
                ProfileItem(Icons.Default.Person, "Personal Information")
                ProfileItem(Icons.Default.Badge, "Professional CV / Bio")
                ProfileItem(Icons.Default.VerifiedUser, "Identity Verification")
                ProfileItem(Icons.Default.GroupAdd, "Manage Organization Team")
            }

            // ⚙️ 3. Preferences Section
            ProfileSection(title = "Preferences") {
                ProfileItem(Icons.Default.Notifications, "Notification Settings")
                ProfileItem(Icons.Default.Lock, "Privacy & Security")
                ProfileItem(Icons.Default.Language, "Language")
            }

            // 💬 4. Support
            ProfileSection(title = "Support") {
                ProfileItem(Icons.Default.HelpOutline, "Help Center")
                ProfileItem(Icons.Default.Info, "About PivotaConnect")
            }

            // 🚪 Sign Out
            TextButton(
                onClick = { /* Sign Out */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out of Account", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ────────────── UI COMPONENTS ────────────── */

@Composable
fun ProfileHeroHeader(teal: Color, gold: Color) {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.nairobi_city),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Branded Teal Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(teal.copy(0.95f), teal.copy(0.8f))
                    )
                )
        )

        // Top Action Icons (Consistency)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            ProfileHeaderIcon(Icons.Default.Mail) { /* Inbox */ }
            Spacer(modifier = Modifier.width(10.dp))
            ProfileHeaderIcon(Icons.Default.Notifications) { /* Notifications */ }
        }

        // Profile Info Center
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Avatar with Gold Border if Verified
            Box(
                modifier = Modifier
                    .size(94.dp)
                    .background(gold, CircleShape) // Gold ring
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = teal, modifier = Modifier.size(50.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Allan Mathenge", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

            // Organization/Individual Role Badge
            Surface(
                color = Color.White.copy(0.15f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(0.3f))
            ) {
                Text(
                    "Organization Admin", // Or "Individual User"
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 1.dp
        ) {
            Column { content() }
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(Color(0xFFF6FAF9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color(0xFF006565), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ProfileHeaderIcon(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color.White.copy(0.2f), CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}