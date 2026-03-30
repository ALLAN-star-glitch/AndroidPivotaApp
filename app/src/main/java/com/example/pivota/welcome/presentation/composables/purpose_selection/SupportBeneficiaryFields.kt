package com.example.pivota.welcome.presentation.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SupportBeneficiaryFields(
    data: SupportBeneficiaryData,
    onDataChange: (SupportBeneficiaryData) -> Unit
) {
    var currentUrgentNeedInput by remember { mutableStateOf("") }
    var currentLocationInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Parse urgent needs from data.urgentNeeds string into a list
    val urgentNeedsList = remember(data.urgentNeeds) {
        data.urgentNeeds.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Parse location from data.location string into a list (if multiple locations)
    val locationList = remember(data.location) {
        data.location.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Generic add function for any list
    fun addItem(currentInput: String, currentList: List<String>, onAdd: (String) -> Unit, onClearInput: () -> Unit) {
        val trimmedItem = currentInput.trim()
        if (trimmedItem.isNotEmpty() && !currentList.contains(trimmedItem)) {
            val newItems = if (currentList.isEmpty()) {
                trimmedItem
            } else {
                "${currentList.joinToString(", ")}, $trimmedItem"
            }
            onAdd(newItems)
            onClearInput()
            showDuplicateError = false
        } else if (currentList.contains(trimmedItem)) {
            showDuplicateError = true
            coroutineScope.launch {
                delay(2000)
                showDuplicateError = false
            }
        }
    }

    // Generic remove function
    fun removeItem(itemToRemove: String, currentList: List<String>, onRemove: (String) -> Unit) {
        val newItems = currentList.filter { it != itemToRemove }.joinToString(", ")
        onRemove(newItems)
    }

    // Support Types options
    val supportOptions = listOf(
        "Food", "Shelter", "Medical", "Counseling",
        "Training", "Legal", "Cash Assistance", "Education",
        "Job Placement", "Transportation", "Childcare"
    )

    // Common urgent needs suggestions
    val urgentNeedSuggestions = listOf(
        "Food for family", "Emergency shelter", "Medical treatment",
        "School fees", "Clothing", "Bedding", "Water", "Electricity"
    )

    // Common location suggestions
    val locationSuggestions = listOf(
        "Kawangware", "Kibera", "Mathare", "Mukuru", "Korogocho",
        "Eastleigh", "Ngara", "Kariobangi", "Huruma", "Dandora"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURPOSE DETAILS: Support Beneficiary",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Support Types (multi-select)
            Text(
                text = "Type of Support Needed",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(supportOptions) { option ->
                    FilterChip(
                        selected = data.supportTypes.contains(option),
                        onClick = {
                            val newList = if (data.supportTypes.contains(option)) {
                                data.supportTypes.filter { it != option }
                            } else {
                                data.supportTypes + option
                            }
                            onDataChange(data.copy(supportTypes = newList))
                        },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Show selected count
            if (data.supportTypes.isNotEmpty()) {
                Text(
                    text = "${data.supportTypes.size} support type${if (data.supportTypes.size > 1) "s" else ""} selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Urgent Needs Section (Pill Input)
            UrgentNeedInputSection(
                title = "Urgent Needs",
                itemsList = urgentNeedsList,
                currentInput = currentUrgentNeedInput,
                onCurrentInputChange = { currentUrgentNeedInput = it },
                onAddItem = { need ->
                    addItem(need, urgentNeedsList, { newNeeds -> onDataChange(data.copy(urgentNeeds = newNeeds)) }) { currentUrgentNeedInput = "" }
                },
                onRemoveItem = { need ->
                    removeItem(need, urgentNeedsList, { newNeeds -> onDataChange(data.copy(urgentNeeds = newNeeds)) })
                },
                placeholder = "e.g., Food for family of 4, Immediate shelter, Medical assistance",
                suggestions = urgentNeedSuggestions,
                showDuplicateError = showDuplicateError
            )

            // Location Section (Pill Input)
            LocationInputSection(
                title = "Location",
                itemsList = locationList,
                currentInput = currentLocationInput,
                onCurrentInputChange = { currentLocationInput = it },
                onAddItem = { location ->
                    addItem(location, locationList, { newLocation -> onDataChange(data.copy(location = newLocation)) }) { currentLocationInput = "" }
                },
                onRemoveItem = { location ->
                    removeItem(location, locationList, { newLocation -> onDataChange(data.copy(location = newLocation)) })
                },
                placeholder = "e.g., Kawangware, Kibera, Mathare",
                suggestions = locationSuggestions,
                showDuplicateError = showDuplicateError
            )

            // Family Size
            OutlinedTextField(
                value = data.familySize,
                onValueChange = { onDataChange(data.copy(familySize = it)) },
                label = { Text("Family Size") },
                placeholder = { Text("4") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
fun UrgentNeedInputSection(
    title: String,
    itemsList: List<String>,
    currentInput: String,
    onCurrentInputChange: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    placeholder: String,
    suggestions: List<String>,
    showDuplicateError: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // Items Pills Row
        if (itemsList.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(itemsList) { item ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                item,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveItem(item) },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Input Field
        OutlinedTextField(
            value = currentInput,
            onValueChange = {
                onCurrentInputChange(it)
            },
            label = {
                Text(
                    if (itemsList.isEmpty()) "Add $title" else "Add another $title",
                    color = if (showDuplicateError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            onAddItem(currentInput)
                        }
                    },
                    enabled = currentInput.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = if (currentInput.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (currentInput.isNotBlank()) {
                        onAddItem(currentInput)
                    }
                }
            ),
            isError = showDuplicateError,
            supportingText = {
                if (showDuplicateError) {
                    Text(
                        "This need has already been added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (itemsList.isNotEmpty()) {
                    Text(
                        "${itemsList.size} need${if (itemsList.size > 1) "s" else ""} added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )

        // Suggestions chips
        if (suggestions.isNotEmpty()) {
            Text(
                text = "Suggestions:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = {
                            if (!itemsList.contains(suggestion)) {
                                onAddItem(suggestion)
                            }
                        },
                        label = {
                            Text(
                                suggestion,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        enabled = !itemsList.contains(suggestion),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LocationInputSection(
    title: String,
    itemsList: List<String>,
    currentInput: String,
    onCurrentInputChange: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    placeholder: String,
    suggestions: List<String>,
    showDuplicateError: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // Items Pills Row
        if (itemsList.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(itemsList) { item ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                item,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveItem(item) },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Input Field
        OutlinedTextField(
            value = currentInput,
            onValueChange = {
                onCurrentInputChange(it)
            },
            label = {
                Text(
                    if (itemsList.isEmpty()) "Add $title" else "Add another $title",
                    color = if (showDuplicateError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            onAddItem(currentInput)
                        }
                    },
                    enabled = currentInput.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = if (currentInput.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (currentInput.isNotBlank()) {
                        onAddItem(currentInput)
                    }
                }
            ),
            isError = showDuplicateError,
            supportingText = {
                if (showDuplicateError) {
                    Text(
                        "This location has already been added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (itemsList.isNotEmpty()) {
                    Text(
                        "${itemsList.size} location${if (itemsList.size > 1) "s" else ""} added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )

        // Suggestions chips
        if (suggestions.isNotEmpty()) {
            Text(
                text = "Suggestions:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = {
                            if (!itemsList.contains(suggestion)) {
                                onAddItem(suggestion)
                            }
                        },
                        label = {
                            Text(
                                suggestion,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        enabled = !itemsList.contains(suggestion),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}