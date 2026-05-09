package com.example.pivota.welcome.presentation.composables.purpose_selection

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.ui.theme.*
import com.example.pivota.welcome.presentation.screens.SuggestionPill
import com.example.pivota.welcome.presentation.state.SkilledProfessionalFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SkilledProfessionalFields(
    data: SkilledProfessionalFormData,
    onDataChange: (SkilledProfessionalFormData) -> Unit
): Boolean {

    var currentSpecialtyInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    var showAddHint by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val specialtiesList = remember(data.specialties) {
        data.specialties.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    val professions = listOf(
        "Electrician", "Plumber", "Carpenter",
        "Welder", "Painter", "Mason"
    )

    val specialtySuggestions = listOf("Wiring", "Pipe Fixing", "Painting", "Welding", "Carpentry", "Drywall")

    val hasSelectedProfession = data.profession.isNotEmpty() &&
            (data.profession != "Other" || data.otherProfession.isNotBlank())

    // Validation logic - profession must be selected
    val isValid = hasSelectedProfession

    // Dynamic placeholder text
    val specialtyPlaceholder = if (specialtiesList.isEmpty())
        "Type a specialty (e.g., Wiring, Plumbing)"
    else
        "Type another specialty"

    fun addSpecialty(input: String) {
        val trimmed = input.trim()
        if (trimmed.isNotEmpty() && !specialtiesList.contains(trimmed)) {
            val updated = if (specialtiesList.isEmpty()) trimmed
            else "${specialtiesList.joinToString(", ")}, $trimmed"
            onDataChange(data.copy(specialties = updated))
            currentSpecialtyInput = ""
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

    fun removeSpecialty(item: String) {
        val updated = specialtiesList.filter { it != item }.joinToString(", ")
        onDataChange(data.copy(specialties = updated))
    }

    fun clearAllSpecialties() {
        onDataChange(data.copy(specialties = ""))
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
                    text = "Skilled\nProfessional",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "Tell clients about your skills",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Profession Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Profession *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                // Profession Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(professions) { profession ->
                        val isSelected = data.profession == profession

                        ElegantPill(
                            selected = isSelected,
                            onClick = {
                                onDataChange(
                                    data.copy(
                                        profession = profession,
                                        otherProfession = ""
                                    )
                                )
                            },
                            label = profession,
                            modifier = Modifier
                        )
                    }

                    item {
                        val isSelected = data.profession == "Other"

                        ElegantPill(
                            selected = isSelected,
                            onClick = {
                                onDataChange(
                                    data.copy(
                                        profession = "Other",
                                        otherProfession = data.otherProfession
                                    )
                                )
                            },
                            label = "Other",
                            modifier = Modifier
                        )
                    }
                }

                // Other Profession Input (conditionally shown)
                AnimatedVisibility(
                    visible = data.profession == "Other",
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
                    OutlinedTextField(
                        value = data.otherProfession,
                        onValueChange = {
                            onDataChange(
                                data.copy(
                                    profession = "Other",
                                    otherProfession = it
                                )
                            )
                        },
                        placeholder = {
                            Text(
                                "Enter your profession",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Validation message for profession
                if (!hasSelectedProfession) {
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
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Please select your profession to continue",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Specialties Section - Animated conditional display (hidden until profession selected)
            AnimatedVisibility(
                visible = hasSelectedProfession,
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                text = "Specialties",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Text(
                                text = "What are you best at? (Optional)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // Counter and Clear button
                        if (specialtiesList.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedContent(
                                    targetState = specialtiesList.size,
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

                                ClearButton(
                                    onClick = { clearAllSpecialties() }
                                )
                            }
                        }
                    }

                    // Specialties Pills Row
                    if (specialtiesList.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(specialtiesList) { specialty ->
                                ElegantPillWithRemove(
                                    label = specialty,
                                    onRemove = { removeSpecialty(specialty) }
                                )
                            }
                        }
                    }

                    // Add Specialty Section - Redesigned with plus icon inside input
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Input field with trailing plus icon
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                BasicTextField(
                                    value = currentSpecialtyInput,
                                    onValueChange = {
                                        currentSpecialtyInput = it
                                        if (it.isNotBlank() && !showAddHint) {
                                            showAddHint = true
                                        } else if (it.isBlank() && showAddHint) {
                                            showAddHint = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .onFocusChanged { isFocused = it.isFocused },
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (currentSpecialtyInput.isNotBlank()) {
                                                addSpecialty(currentSpecialtyInput)
                                            }
                                        }
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    decorationBox = { innerTextField ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                if (currentSpecialtyInput.isEmpty()) {
                                                    Text(
                                                        text = specialtyPlaceholder,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontSize = 14.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                        )
                                                    )
                                                }
                                                innerTextField()
                                            }

                                            AnimatedVisibility(
                                                visible = currentSpecialtyInput.isNotBlank(),
                                                enter = scaleIn() + fadeIn(),
                                                exit = scaleOut() + fadeOut()
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        if (currentSpecialtyInput.isNotBlank()) {
                                                            addSpecialty(currentSpecialtyInput)
                                                        }
                                                    },
                                                    modifier = Modifier.size(40.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = "Add Specialty",
                                                        modifier = Modifier.size(24.dp),
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )

                                // Custom border for the input field
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            width = 1.dp,
                                            color = when {
                                                showDuplicateError -> MaterialTheme.colorScheme.error
                                                isFocused -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                )
                            }

                            // Error or hint message
                            if (showDuplicateError) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Error",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "This specialty has already been added",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    )
                                }
                            } else if (showAddHint && currentSpecialtyInput.isNotBlank()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Hint",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "Press the + button or Done key to add",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Suggestions (preserved)
                    if (specialtySuggestions.isNotEmpty()) {
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
                                items(specialtySuggestions) { suggestion ->
                                    val isAlreadyAdded = specialtiesList.contains(suggestion)

                                    SuggestionPill(
                                        label = suggestion,
                                        isAdded = isAlreadyAdded,
                                        onClick = {
                                            if (!isAlreadyAdded) {
                                                addSpecialty(suggestion)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    return isValid
}