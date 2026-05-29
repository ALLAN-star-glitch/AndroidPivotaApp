package com.example.pivota.core.presentations.composables

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pivota.ui.theme.InfoBlue
import com.example.pivota.ui.theme.SuccessGreen
import com.example.pivota.ui.theme.WarningAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PivotaSnackbar(
    message: String,
    type: SnackbarType = SnackbarType.ERROR,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},
    duration: Long = 4000,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(message) {
        if (message.isNotBlank()) {
            delay(duration)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = message.isNotBlank(),
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Card(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .zIndex(2000f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (type) {
                    SnackbarType.ERROR -> colorScheme.error
                    SnackbarType.SUCCESS -> SuccessGreen  // Use dedicated success color
                    SnackbarType.WARNING -> WarningAmber  // Use dedicated warning color
                    SnackbarType.INFO -> InfoBlue        // Use dedicated info color
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (type) {
                        SnackbarType.ERROR -> Icons.Default.Error
                        SnackbarType.SUCCESS -> Icons.Default.CheckCircle
                        SnackbarType.WARNING -> Icons.Default.Warning
                        SnackbarType.INFO -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Action button - compact styling
                if (actionText != null && onAction != null) {
                    Button(
                        onClick = onAction,
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.surface,
                            contentColor = when (type) {
                                SnackbarType.ERROR -> colorScheme.error
                                SnackbarType.SUCCESS -> SuccessGreen
                                SnackbarType.WARNING -> WarningAmber
                                SnackbarType.INFO -> InfoBlue
                            }
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = actionText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

enum class SnackbarType {
    ERROR,
    SUCCESS,
    WARNING,
    INFO
}