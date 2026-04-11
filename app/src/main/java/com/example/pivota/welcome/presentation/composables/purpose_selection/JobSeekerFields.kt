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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import com.example.pivota.welcome.presentation.state.JobSeekerFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun JobSeekerFields(
    data: JobSeekerFormData,
    onDataChange: (JobSeekerFormData) -> Unit
) {
    var currentSkillInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    var showAddHint by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val skillsList = remember(data.skills) {
        data.skills.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    val suggestions = listOf("Welding", "Driving", "Plumbing", "Electrical", "Carpentry", "Painting")

    fun addSkill(input: String) {
        val trimmed = input.trim()
        if (trimmed.isNotEmpty() && !skillsList.contains(trimmed)) {
            val updated = if (skillsList.isEmpty()) trimmed
            else "${skillsList.joinToString(", ")}, $trimmed"

            onDataChange(data.copy(skills = updated))
            currentSkillInput = ""
            showDuplicateError = false
            showAddHint = false
        } else if (trimmed.isNotEmpty()) {
            showDuplicateError = true
            coroutineScope.launch {
                delay(1500)
                showDuplicateError = false
            }
        }
    }

    fun removeSkill(skill: String) {
        val updated = skillsList.filter { it != skill }.joinToString(", ")
        onDataChange(data.copy(skills = updated))
    }

    fun clearAllSkills() {
        onDataChange(data.copy(skills = ""))
    }

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
                    text = "Quick Setup",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "Tell employers about yourself",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Headline Field
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "What do you do?",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                OutlinedTextField(
                    value = data.headline,
                    onValueChange = { onDataChange(data.copy(headline = it)) },
                    placeholder = {
                        Text(
                            "e.g., Welder, Driver, Electrician",
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
                        Text(
                            "A short description of your work",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                )
            }

            // Skills Section with Elegant Pills
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Skills",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = "Add your key skills",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Skills counter with clear button next to it
                    if (skillsList.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Animated counter
                            AnimatedContent(
                                targetState = skillsList.size,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(200)) +
                                            scaleIn(initialScale = 0.7f) togetherWith
                                            fadeOut(animationSpec = tween(100)) +
                                            scaleOut(targetScale = 0.7f)
                                }
                            ) { count ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier
                                ) {
                                    Text(
                                        text = "$count",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }

                            // Clear button right next to counter
                            ClearButton(
                                onClick = { clearAllSkills() }
                            )
                        }
                    }
                }

                // Skills Pills Row
                if (skillsList.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(skillsList) { skill ->
                            ElegantSkillPill(
                                label = skill,
                                onRemove = { removeSkill(skill) }
                            )
                        }
                    }
                }

                // Add Skill Input with better UX
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = currentSkillInput,
                        onValueChange = {
                            currentSkillInput = it
                            if (it.isNotBlank() && !showAddHint) {
                                showAddHint = true
                            } else if (it.isBlank() && showAddHint) {
                                showAddHint = false
                            }
                        },
                        placeholder = {
                            Text(
                                if (skillsList.isEmpty()) "Type a skill and tap + to add" else "Type another skill and tap +",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp
                                )
                            )
                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = currentSkillInput.isNotBlank(),
                                enter = scaleIn() + fadeIn(),
                                exit = scaleOut() + fadeOut()
                            ) {
                                IconButton(
                                    onClick = { addSkill(currentSkillInput) }
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add skill",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (currentSkillInput.isNotBlank()) {
                                    addSkill(currentSkillInput)
                                }
                            }
                        ),
                        isError = showDuplicateError,
                        supportingText = {
                            if (showDuplicateError) {
                                Text(
                                    "This skill has already been added",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp
                                )
                            } else if (showAddHint && currentSkillInput.isNotBlank()) {
                                Text(
                                    "Tap the + button or press Done to add",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            errorLabelColor = MaterialTheme.colorScheme.error
                        )
                    )
                }

                // Suggestions
                if (suggestions.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Quick suggestions",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                letterSpacing = 0.4.sp
                            )
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(suggestions) { suggestion ->
                                val isAlreadyAdded = skillsList.contains(suggestion)

                                SuggestionPill(
                                    label = suggestion,
                                    isAdded = isAlreadyAdded,
                                    onClick = {
                                        if (!isAlreadyAdded) {
                                            addSkill(suggestion)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Actively Seeking Section
            Divider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Actively Seeking",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Text(
                        text = "Let employers know you're available",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Switch(
                    checked = data.isActivelySeeking,
                    onCheckedChange = {
                        onDataChange(data.copy(isActivelySeeking = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.surface,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
fun ClearButton(
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Surface(
        modifier = Modifier
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                text = "Clear",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    }
}

@Composable
fun ElegantSkillPill(
    label: String,
    onRemove: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    Surface(
        modifier = Modifier
            .scale(scale),
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ),
        onClick = { }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        isPressed = true
                        onRemove()
                        delay(80)
                        isPressed = false
                    }
                },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun SuggestionPill(
    label: String,
    isAdded: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isAdded)
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "backgroundColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isAdded)
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "textColor"
    )

    Surface(
        modifier = Modifier
            .scale(scale),
        shape = RoundedCornerShape(100.dp),
        color = backgroundColor,
        onClick = {
            if (!isAdded) {
                coroutineScope.launch {
                    isPressed = true
                    onClick()
                    delay(80)
                    isPressed = false
                }
            }
        },
        enabled = !isAdded
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = textColor
                )
            )

            if (!isAdded) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(12.dp),
                    tint = textColor
                )
            }
        }
    }
}