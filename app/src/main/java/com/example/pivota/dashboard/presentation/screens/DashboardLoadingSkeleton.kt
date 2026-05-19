package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pivota.ui.theme.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardLoadingSkeleton() {
    val colorScheme = MaterialTheme.colorScheme
    val skeletonColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val shimmerGradient = listOf(
        skeletonColor,
        skeletonColor.copy(alpha = 0.2f),
        skeletonColor
    )

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            // Header skeleton - matches ReusableHeader
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color.Black.copy(alpha = 0.08f),
                        spotColor = Color.Black.copy(alpha = 0.06f)
                    ),
                shape = RoundedCornerShape(24.dp),
                color = colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side - Profile Avatar and User Info
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar skeleton
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(skeletonColor)
                        )

                        // User info skeletons
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(skeletonColor)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(skeletonColor)
                            )
                        }
                    }

                    // Right side - Action icons skeletons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(skeletonColor)
                        )
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(skeletonColor)
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Marketing Carousel Banner Skeleton
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = shimmerGradient,
                                startX = 0f,
                                endX = 1000f
                            )
                        )
                )
            }

            // Spacer
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Search Bar Skeleton
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(skeletonColor)
                )
            }

            // Filter Pills Row Skeleton
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(skeletonColor)
                        )
                    }
                }
            }

            // Common Services Section Skeleton
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section header skeleton
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                    }

                    // Service grid skeleton (4 items per row)
                    repeat(2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(4) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(skeletonColor)
                                )
                            }
                        }
                    }
                }
            }

            // Jobs Section Skeleton
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section header skeleton
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                    }

                    // Job cards grid skeleton (2 columns)
                    repeat(3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(2) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(skeletonColor)
                                )
                            }
                        }
                    }
                }
            }

            // Housing Section Skeleton
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section header skeleton
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                    }

                    // Housing cards grid skeleton (2 columns)
                    repeat(3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(2) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(220.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(skeletonColor)
                                )
                            }
                        }
                    }
                }
            }

            // Professionals Section Skeleton
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section header skeleton
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                    }

                    // Professional cards grid skeleton (2 columns)
                    repeat(3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(2) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(skeletonColor)
                                )
                            }
                        }
                    }
                }
            }

            // Social Support Section Skeleton
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section header skeleton
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(160.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                    }

                    // Support cards skeleton
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .margin(horizontal = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(skeletonColor)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

// Extension function for margin
private fun Modifier.margin(horizontal: androidx.compose.ui.unit.Dp): Modifier = this.then(
    Modifier.padding(start = horizontal, end = horizontal)
)