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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.welcome.presentation.state.PropertyOwnerFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PropertyOwnerFields(
    data: PropertyOwnerFormData,
    onDataChange: (PropertyOwnerFormData) -> Unit
) {
    var currentPropertyTypeInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val propertyTypesList = remember(data.propertyTypes) {
        data.propertyTypes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    // Professional Status options
    val professionalStatuses = listOf(
        "Individual Owner",
        "Professional Landlord",
        "Property Manager",
        "Investor"
    )

    // Suggestions for property types
    val propertyTypeSuggestions = listOf(
        "Apartments",
        "Single Family Homes",
        "Commercial Spaces",
        "Land"
    )

    fun addPropertyType(input: String) {
        val trimmed = input.trim()
        if (trimmed.isNotEmpty() && !propertyTypesList.contains(trimmed)) {
            val updated = if (propertyTypesList.isEmpty()) trimmed else "${propertyTypesList.joinToString(", ")}, $trimmed"
            onDataChange(data.copy(propertyTypes = updated))
            currentPropertyTypeInput = ""
            showDuplicateError = false
        } else {
            showDuplicateError = true
            coroutineScope.launch {
                delay(1500)
                showDuplicateError = false
            }
        }
    }

    fun removePropertyType(item: String) {
        val updated = propertyTypesList.filter { it != item }.joinToString(", ")
        onDataChange(data.copy(propertyTypes = updated))
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Property Owner Details",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Professional Status
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(professionalStatuses) { status ->
                    FilterChip(
                        selected = data.professionalStatus == status,
                        onClick = { onDataChange(data.copy(professionalStatus = status)) },
                        label = { Text(status) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Property Types (Pill Input)
            PropertyTypeInputSection(
                title = "Property Types Owned",
                itemsList = propertyTypesList,
                currentInput = currentPropertyTypeInput,
                onCurrentInputChange = { currentPropertyTypeInput = it },
                onAddItem = { addPropertyType(it) },
                onRemoveItem = { removePropertyType(it) },
                placeholder = "e.g., Apartments, Commercial Spaces, Land",
                suggestions = propertyTypeSuggestions,
                showDuplicateError = showDuplicateError
            )
        }
    }
}

@Composable
fun PropertyTypeInputSection(
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // Pills Row
        if (itemsList.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(itemsList) { item ->
                    AssistChip(
                        onClick = {},
                        label = { Text(item, maxLines = 1, style = MaterialTheme.typography.bodyMedium) },
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
        }

        // Input Field
        OutlinedTextField(
            value = currentInput,
            onValueChange = onCurrentInputChange,
            label = {
                Text(
                    if (itemsList.isEmpty()) "Add $title" else "Add another $title",
                    color = if (showDuplicateError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            placeholder = { Text(placeholder) },
            trailingIcon = {
                IconButton(
                    onClick = { if (currentInput.isNotBlank()) onAddItem(currentInput) },
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
                onDone = { if (currentInput.isNotBlank()) onAddItem(currentInput) }
            ),
            isError = showDuplicateError,
            supportingText = {
                if (showDuplicateError) {
                    Text(
                        "This item has already been added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (itemsList.isNotEmpty()) {
                    Text(
                        "${itemsList.size} item${if (itemsList.size > 1) "s" else ""} added",
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

        // Suggestion Chips
        if (suggestions.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = { if (!itemsList.contains(suggestion)) onAddItem(suggestion) },
                        label = { Text(suggestion) },
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