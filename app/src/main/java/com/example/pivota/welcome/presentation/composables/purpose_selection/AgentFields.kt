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
import androidx.compose.ui.draw.shadow
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
import com.example.pivota.welcome.presentation.state.AgentFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AgentFields(
    data: AgentFormData,
    onDataChange: (AgentFormData) -> Unit
) {
    var currentSpecializationInput by remember { mutableStateOf("") }
    var currentServiceAreaInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    var errorField by remember { mutableStateOf("") }
    var isSpecFocused by remember { mutableStateOf(false) }
    var isAreaFocused by remember { mutableStateOf(false) }
    var showSpecAddHint by remember { mutableStateOf(false) }
    var showAreaAddHint by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val specializationsList = remember(data.specializations) {
        data.specializations.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    val serviceAreasList = remember(data.serviceAreas) {
        data.serviceAreas.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    val agentTypes = listOf("Housing Agent", "Recruitment Agent", "Broker", "Insurance Agent", "Real Estate Agent")

    // Dynamic placeholders
    val specPlaceholder = if (specializationsList.isEmpty())
        "Type a specialization (e.g., Residential, Commercial)"
    else
        "Type another specialization"

    val areaPlaceholder = if (serviceAreasList.isEmpty())
        "Type a service area (e.g., Nairobi, Kiambu)"
    else
        "Type another area"

    fun addItem(currentInput: String, currentList: List<String>, onAdd: (String) -> Unit, onClearInput: () -> Unit, field: String) {
        val trimmedItem = currentInput.trim()
        if (trimmedItem.isNotEmpty() && !currentList.contains(trimmedItem)) {
            val newItems = if (currentList.isEmpty()) {
                trimmedItem
            } else {
                "${currentList.joinToString(", ")}, $trimmedItem"
            }
            onAdd(newItems)
            onClearInput()
            if (showDuplicateError && errorField == field) {
                showDuplicateError = false
            }
        } else if (currentList.contains(trimmedItem)) {
            showDuplicateError = true
            errorField = field
            coroutineScope.launch {
                delay(2000)
                showDuplicateError = false
                errorField = ""
            }
        }
    }

    fun removeItem(itemToRemove: String, currentList: List<String>, onRemove: (String) -> Unit) {
        val newItems = currentList.filter { it != itemToRemove }.joinToString(", ")
        onRemove(newItems)
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(24.dp),
            )
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
                    text = "Agent Profile",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "Tell clients about your services",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Agent Type - Elegant Pills Row
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Agent Type",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(agentTypes) { type ->
                        val isSelected = data.agentType == type

                        ElegantPill(
                            selected = isSelected,
                            onClick = { onDataChange(data.copy(agentType = type)) },
                            label = type,
                            modifier = Modifier
                        )
                    }
                }
            }

            // Specializations Section - Redesigned
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
                            text = "Specializations",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = "What areas do you specialize in?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    if (specializationsList.isNotEmpty()) {
                        AnimatedContent(
                            targetState = specializationsList.size,
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
                    }
                }

                // Specialization Pills
                if (specializationsList.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(specializationsList) { spec ->
                            ElegantPillWithRemove(
                                label = spec,
                                onRemove = { removeItem(spec, specializationsList) { newSpecs -> onDataChange(data.copy(specializations = newSpecs)) } }
                            )
                        }
                    }
                }

                // Redesigned Specialization Input Field
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            BasicTextField(
                                value = currentSpecializationInput,
                                onValueChange = {
                                    currentSpecializationInput = it
                                    if (it.isNotBlank() && !showSpecAddHint) {
                                        showSpecAddHint = true
                                    } else if (it.isBlank() && showSpecAddHint) {
                                        showSpecAddHint = false
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onFocusChanged { isSpecFocused = it.isFocused },
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
                                        if (currentSpecializationInput.isNotBlank()) {
                                            addItem(
                                                currentSpecializationInput,
                                                specializationsList,
                                                { newSpecs -> onDataChange(data.copy(specializations = newSpecs)) },
                                                { currentSpecializationInput = "" },
                                                "spec"
                                            )
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
                                            if (currentSpecializationInput.isEmpty()) {
                                                Text(
                                                    text = specPlaceholder,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                    )
                                                )
                                            }
                                            innerTextField()
                                        }

                                        AnimatedVisibility(
                                            visible = currentSpecializationInput.isNotBlank(),
                                            enter = scaleIn() + fadeIn(),
                                            exit = scaleOut() + fadeOut()
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    if (currentSpecializationInput.isNotBlank()) {
                                                        addItem(
                                                            currentSpecializationInput,
                                                            specializationsList,
                                                            { newSpecs -> onDataChange(data.copy(specializations = newSpecs)) },
                                                            { currentSpecializationInput = "" },
                                                            "spec"
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.size(40.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Specialization",
                                                    modifier = Modifier.size(24.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            )

                            // Custom border
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            showDuplicateError && errorField == "spec" -> MaterialTheme.colorScheme.error
                                            isSpecFocused -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            )
                        }

                        // Error or hint message
                        if (showDuplicateError && errorField == "spec") {
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
                                    text = "This specialization has already been added",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        } else if (showSpecAddHint && currentSpecializationInput.isNotBlank()) {
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

                // Suggestions for Specializations
                val specSuggestions = listOf("Residential", "Commercial", "Luxury", "Industrial", "Agricultural", "Land")
                if (specSuggestions.isNotEmpty()) {
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
                            items(specSuggestions) { suggestion ->
                                val isAlreadyAdded = specializationsList.contains(suggestion)

                                SuggestionPill(
                                    label = suggestion,
                                    isAdded = isAlreadyAdded,
                                    onClick = {
                                        if (!isAlreadyAdded) {
                                            addItem(
                                                suggestion,
                                                specializationsList,
                                                { newSpecs -> onDataChange(data.copy(specializations = newSpecs)) },
                                                {},
                                                "spec"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Service Areas Section - Redesigned
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
                            text = "Service Areas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = "Where do you provide services?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    if (serviceAreasList.isNotEmpty()) {
                        AnimatedContent(
                            targetState = serviceAreasList.size,
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
                    }
                }

                // Service Area Pills
                if (serviceAreasList.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(serviceAreasList) { area ->
                            ElegantPillWithRemove(
                                label = area,
                                onRemove = { removeItem(area, serviceAreasList) { newAreas -> onDataChange(data.copy(serviceAreas = newAreas)) } }
                            )
                        }
                    }
                }

                // Redesigned Service Area Input Field
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            BasicTextField(
                                value = currentServiceAreaInput,
                                onValueChange = {
                                    currentServiceAreaInput = it
                                    if (it.isNotBlank() && !showAreaAddHint) {
                                        showAreaAddHint = true
                                    } else if (it.isBlank() && showAreaAddHint) {
                                        showAreaAddHint = false
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onFocusChanged { isAreaFocused = it.isFocused },
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
                                        if (currentServiceAreaInput.isNotBlank()) {
                                            addItem(
                                                currentServiceAreaInput,
                                                serviceAreasList,
                                                { newAreas -> onDataChange(data.copy(serviceAreas = newAreas)) },
                                                { currentServiceAreaInput = "" },
                                                "area"
                                            )
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
                                            if (currentServiceAreaInput.isEmpty()) {
                                                Text(
                                                    text = areaPlaceholder,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                    )
                                                )
                                            }
                                            innerTextField()
                                        }

                                        AnimatedVisibility(
                                            visible = currentServiceAreaInput.isNotBlank(),
                                            enter = scaleIn() + fadeIn(),
                                            exit = scaleOut() + fadeOut()
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    if (currentServiceAreaInput.isNotBlank()) {
                                                        addItem(
                                                            currentServiceAreaInput,
                                                            serviceAreasList,
                                                            { newAreas -> onDataChange(data.copy(serviceAreas = newAreas)) },
                                                            { currentServiceAreaInput = "" },
                                                            "area"
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.size(40.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Area",
                                                    modifier = Modifier.size(24.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            )

                            // Custom border
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            showDuplicateError && errorField == "area" -> MaterialTheme.colorScheme.error
                                            isAreaFocused -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            )
                        }

                        // Error or hint message
                        if (showDuplicateError && errorField == "area") {
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
                                    text = "This service area has already been added",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        } else if (showAreaAddHint && currentServiceAreaInput.isNotBlank()) {
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

                // Suggestions for Service Areas
                val areaSuggestions = listOf("Nairobi", "Kiambu", "Kajiado", "Machakos", "Mombasa", "Kisumu")
                if (areaSuggestions.isNotEmpty()) {
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
                            items(areaSuggestions) { suggestion ->
                                val isAlreadyAdded = serviceAreasList.contains(suggestion)

                                SuggestionPill(
                                    label = suggestion,
                                    isAdded = isAlreadyAdded,
                                    onClick = {
                                        if (!isAlreadyAdded) {
                                            addItem(
                                                suggestion,
                                                serviceAreasList,
                                                { newAreas -> onDataChange(data.copy(serviceAreas = newAreas)) },
                                                {},
                                                "area"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Commission Rate
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Commission Rate (%)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                OutlinedTextField(
                    value = data.commissionRate,
                    onValueChange = { onDataChange(data.copy(commissionRate = it)) },
                    placeholder = {
                        Text(
                            "Enter commission percentage",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun ElegantPillWithRemove(
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