package com.example.pivota.listings.presentation.composables.jobs


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.listings.presentation.viewmodel.PostJobViewModel

@Composable
fun PostJobActionButtons(
    viewModel: PostJobViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🚀 Primary Action: Post Job
        Button(
            onClick = { viewModel.submitJob() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp), // Consistent with Auth Screen
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF006565), // Pivota Teal
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF006565).copy(alpha = 0.5f)
            ),
            enabled = !uiState.isLoading && uiState.title.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Post Job",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 💡 Freemium/Subscription Hint
        // This links to your SubscriptionPlan (FREE_FOREVER, GOLD, etc.)
        Text(
            text = "Your job will be visible to verified workers in ${uiState.locationCity}.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Upgrade CTA - Minimalist & Professional
        TextButton(
            onClick = { /* Navigate to Subscriptions */ },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Reach more workers? Upgrade to Gold",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF006565),
                fontWeight = FontWeight.Bold
            )
        }
    }
}