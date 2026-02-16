package com.example.pivota.auth.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay

@Composable
fun InterestsScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Box(modifier = Modifier.fillMaxSize()) {

        if (isWide) {
            /* ───────── TWO PANE LAYOUT (Tablet/Desktop) ───────── */
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    BackgroundImageAndOverlay(
                        isWideScreen = true,
                        header = "Tailored for You",
                        desc1 = "Select your interests to customize your feed.",
                        showUpgradeButton = false,
                        enableCarousel = false,
                        image = R.drawable.interests
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White)
                ) {
                    InterestsContent(onBack = onBack, onSave = onSave)
                }
            }
        } else {
            /* ───────── SINGLE PANE LAYOUT (Mobile Portrait) ───────── */
            Box(modifier = Modifier.fillMaxSize()) {

                // Background image + teal overlay
                BackgroundImageAndOverlay(
                    isWideScreen = false,
                    header = "Your Interests",
                    desc1 = "Tell us what matters to you",
                    showUpgradeButton = false,
                    enableCarousel = false,
                    image = R.drawable.interests
                )

                // Bottom content card (does NOT cover overlay)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 240.dp), // allows overlay to remain visible
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        InterestsContent(
                            onBack = onBack,
                            onSave = onSave
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestsContent(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var selectedInterests by remember { mutableStateOf(setOf("Housing")) }
    var selectedFocus by remember { mutableStateOf("Near me") }
    var specificInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Adjust top margin for mobile since the overlay is gone
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // Header with Skip button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your interests", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Skip",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBack() }
                )
            }
            Text(
                text = "You can change these anytime in settings.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Interests Selection
        item {
            SectionWrapper("What are you interested in?") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Housing", "Jobs", "Services", "Support", "Service Providers").forEach { item ->
                        InterestChip(
                            label = item,
                            isSelected = selectedInterests.contains(item),
                            onToggle = {
                                selectedInterests = if (selectedInterests.contains(item))
                                    selectedInterests - item else selectedInterests + item
                            }
                        )
                    }
                }
            }
        }

        // Location Focus
        item {
            SectionWrapper("Where should we focus?") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Near me", "My city", "My country", "Remote / Online").forEach { locale ->
                        InterestChip(
                            label = locale,
                            isSelected = selectedFocus == locale,
                            onToggle = { selectedFocus = locale }
                        )
                    }
                }
            }
        }

        // Optional specific input
        item {
            SectionWrapper("Anything specific? (Optional)") {
                // Using standard OutlinedTextField as per your screen design
                OutlinedTextField(
                    value = specificInput,
                    onValueChange = { specificInput = it },
                    placeholder = { Text("e.g. Remote tech jobs", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Save Button
        item {
            Button(
                onClick = { onSave() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
            ) {
                Text("Save preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionWrapper(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
        content()
    }
}

@Composable
fun InterestChip(label: String, isSelected: Boolean, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onToggle() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color(0xFFF5F5F5),
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.DarkGray,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}