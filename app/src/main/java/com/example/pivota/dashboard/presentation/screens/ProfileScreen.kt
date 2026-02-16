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
import androidx.compose.runtime.*
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

/**
 * COMPLETE PROFILE SCREEN
 * Aligned with PivotaConnect MVP1: Capability-Based Identity
 */
@Composable
fun ProfileScreen() {
    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)

    // Mock Subscription Data: In production, these come from your Auth/Subscription State
    val activePlanModules = listOf("houses", "jobs", "service-offerings")
    val isContractorEnabled = activePlanModules.contains("service-offerings")
    val isVerified = true

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
            // 🔝 1. Hero Header with Dynamic Identity Selection
            ProfileHeroHeader(primaryTeal, goldenAccent, activePlanModules, isVerified)

            Spacer(modifier = Modifier.height(24.dp))

            // 🛠 2. PROFESSIONAL SERVICE CONSOLE
            // This represents the "ContractorProfile" layer for MVP1 SmartMatch™
            if (isContractorEnabled) {
                ProfileSection(title = "Professional Service Console") {
                    ProfileItem(Icons.Default.Build, "Manage Specialties")
                    ProfileItem(Icons.Default.History, "Work Experience")
                    ProfileItem(Icons.Default.Map, "Service Coverage Areas")
                    ProfileItem(Icons.Default.Star, "Ratings & SmartMatch™ Insights")
                }
            }

            // 👤 3. Account Management
            ProfileSection(title = "Account Management") {
                ProfileItem(Icons.Default.Person, "Personal Information")
                ProfileItem(Icons.Default.Badge, "Professional CV / Documents")
                ProfileItem(Icons.Default.VerifiedUser, "Identity Verification Status")
                ProfileItem(Icons.Default.GroupAdd, "Manage Organization Team")
            }

            // ⚙️ 4. Financials & Settings (Handles Plan B/Subscription logic)
            ProfileSection(title = "Preferences & Billing") {
                ProfileItem(Icons.Default.Payment, "Subscription Plan")
                ProfileItem(Icons.Default.Notifications, "Notification Settings")
                ProfileItem(Icons.Default.Lock, "Privacy & Security")
            }

            // 💬 5. Support
            ProfileSection(title = "Support") {
                ProfileItem(Icons.Default.HelpOutline, "Help Center")
                ProfileItem(Icons.Default.Info, "About PivotaConnect")
            }

            // 🚪 Sign Out
            TextButton(
                onClick = { /* Sign Out Logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out of Account", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ────────────── UI COMPONENTS ────────────── */

@Composable
fun ProfileHeroHeader(teal: Color, gold: Color, modules: List<String>, isVerified: Boolean) {
    // Dynamically filter titles based on Plan capabilities
    val titleOptions = remember(modules) {
        mutableListOf<String>().apply {
            if (modules.contains("houses")) add("Property Owner")
            if (modules.contains("jobs")) add("Recruiter / Employer")
            if (modules.contains("help-and-support")) add("NGO Partner")
            if (modules.contains("service-offerings")) add("Professional Contractor")
        }.ifEmpty { listOf("Individual Member") }
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedTitle by remember { mutableStateOf(titleOptions.first()) }

    Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
        // Hero Background
        Image(
            painter = painterResource(id = R.drawable.nairobi_city),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Teal Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(teal.copy(0.9f), teal.copy(0.7f))
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Avatar with Verification Status Ring
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(if (isVerified) gold else Color.White.copy(0.3f), CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = teal, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Allan Mathenge",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // CAPABILITY DROPDOWN: User picks their public "Persona"
            Box(modifier = Modifier.padding(top = 8.dp)) {
                Surface(
                    onClick = { expanded = true },
                    color = Color.White.copy(0.2f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.White.copy(0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedTitle, fontSize = 12.sp, color = Color.White)
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    titleOptions.forEach { title ->
                        DropdownMenuItem(
                            text = { Text(title, color = teal, fontSize = 14.sp) },
                            onClick = {
                                selectedTitle = title
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(
            text = title.uppercase(),
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
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
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
            .clickable { /* Navigate to detail screen */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF6FAF9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF006565),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}