package com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.jobs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmartMatchGuidanceBanner() {
    val colorScheme = MaterialTheme.colorScheme

    // Local state to handle dismissal within the session
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            color = colorScheme.primary, // Pivota Teal / African Sapphire
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold SmartMatch Badge Icon - Using tertiary color (Baobab Gold)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = colorScheme.tertiary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "SmartMatch Insight",
                        tint = colorScheme.tertiary, // Baobab Gold
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SmartMatch™ Tip",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.tertiary, // Baobab Gold
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Jobs with clear pay, location & benefits get 3x more responses from verified workers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimary.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }

                IconButton(
                    onClick = { isVisible = false },
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = colorScheme.onPrimary.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}