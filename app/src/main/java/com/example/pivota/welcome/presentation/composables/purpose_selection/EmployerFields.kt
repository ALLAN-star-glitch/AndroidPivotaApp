package com.example.pivota.welcome.presentation.composables.purpose_selection

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.ui.theme.*
import com.example.pivota.welcome.presentation.state.EmployerFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EmployerFields(
    data: EmployerFormData,
    onDataChange: (EmployerFormData) -> Unit
): Boolean { // Return validation state

    val industries = listOf(
        "Construction", "Tech", "Healthcare",
        "Manufacturing", "Education",
        "Agriculture", "Hospitality", "Retail"
    )

    // Validation logic - button enabled when business name is not empty AND industry is selected
    val isValid = data.businessName.isNotBlank() &&
            data.industrySector.isNotEmpty() &&
            (data.industrySector != "Other" || data.otherIndustry.isNotBlank())

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Elegant Header
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            )
                        )
                )

                Text(
                    text = "Employer\nProfile",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "Tell job seekers about your company",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Business Name Field
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Business Name *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                OutlinedTextField(
                    value = data.businessName,
                    onValueChange = { onDataChange(data.copy(businessName = it)) },
                    placeholder = {
                        Text(
                            "e.g., Wanjiku Hardware",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    supportingText = {
                        if (data.businessName.isBlank()) {
                            Text(
                                "Enter your business or company name",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                )
            }

            // Industry Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Industry *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                // Industry Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(industries) { industry ->
                        val isSelected = data.industrySector == industry

                        ElegantPill(
                            selected = isSelected,
                            onClick = {
                                onDataChange(
                                    data.copy(
                                        industrySector = industry,
                                        otherIndustry = if (industry != "Other") "" else data.otherIndustry
                                    )
                                )
                            },
                            label = industry,
                            modifier = Modifier
                        )
                    }

                    item {
                        val isSelected = data.industrySector == "Other"

                        ElegantPill(
                            selected = isSelected,
                            onClick = {
                                onDataChange(
                                    data.copy(
                                        industrySector = "Other",
                                        otherIndustry = data.otherIndustry
                                    )
                                )
                            },
                            label = "Other",
                            modifier = Modifier
                        )
                    }
                }

                // Other Industry Input (conditionally shown)
                AnimatedVisibility(
                    visible = data.industrySector == "Other",
                    enter = fadeIn(animationSpec = tween(400)) +
                            slideInVertically(
                                initialOffsetY = { 30 },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) +
                            scaleIn(
                                initialScale = 0.95f,
                                animationSpec = tween(400)
                            ),
                    exit = fadeOut(animationSpec = tween(200)) +
                            slideOutVertically(
                                targetOffsetY = { -20 },
                                animationSpec = tween(200)
                            ) +
                            scaleOut(
                                targetScale = 0.98f,
                                animationSpec = tween(200)
                            )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Specify Industry",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        )

                        OutlinedTextField(
                            value = data.otherIndustry,
                            onValueChange = { onDataChange(data.copy(otherIndustry = it)) },
                            placeholder = {
                                Text(
                                    "Enter your industry",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { /* Just dismiss keyboard */ }
                            )
                        )
                    }
                }

                // Validation message for industry
                if (data.industrySector.isEmpty()) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInVertically(initialOffsetY = { 20 })
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Please select an industry to continue",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        }
                    }
                }

                // Validation message for other industry
                if (data.industrySector == "Other" && data.otherIndustry.isBlank()) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInVertically(initialOffsetY = { 20 })
                    ) {
                        Text(
                            text = "⚠️ Please specify your industry",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.alpha(0.8f)
                        )
                    }
                }
            }
        }
    }

    return isValid
}

@Composable
fun ElegantPillEmployer(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.97f
            selected -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "backgroundColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250),
        label = "textColor"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (selected) 0.8f else 0.1f,
        animationSpec = tween(250),
        label = "borderAlpha"
    )

    Surface(
        modifier = modifier
            .scale(scale),
        shape = RoundedCornerShape(100.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha)
        ),
        onClick = {
            coroutineScope.launch {
                isPressed = true
                onClick()
                delay(80)
                isPressed = false
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp,
                    color = textColor
                )
            )

            AnimatedVisibility(
                visible = selected,
                enter = scaleIn(initialScale = 0.5f) + fadeIn(),
                exit = scaleOut(targetScale = 0.5f) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}