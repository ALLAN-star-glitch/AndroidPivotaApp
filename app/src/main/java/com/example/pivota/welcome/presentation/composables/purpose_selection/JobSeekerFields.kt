package com.example.pivota.welcome.presentation.composables.purpose_selection

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
import com.example.pivota.welcome.presentation.screens.JobSeekerData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JobSeekerFields(
    data: JobSeekerData,
    onDataChange: (JobSeekerData) -> Unit
) {
    var currentSkillInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val skillsList = remember(data.skills) {
        data.skills.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun addSkill(input: String) {
        val trimmed = input.trim()
        if (trimmed.isNotEmpty() && !skillsList.contains(trimmed)) {
            val updated = if (skillsList.isEmpty()) trimmed
            else "${skillsList.joinToString(", ")}, $trimmed"

            onDataChange(data.copy(skills = updated))
            currentSkillInput = ""
            showDuplicateError = false
        } else {
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
                text = "Quick Setup",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Headline
            OutlinedTextField(
                value = data.headline,
                onValueChange = { onDataChange(data.copy(headline = it)) },
                label = { Text("What do you do?") },
                placeholder = { Text("e.g., Welder, Driver, Electrician") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    Text(
                        "A short description of your work",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            // Skills
            SkillInputSection(
                title = "Skills",
                itemsList = skillsList,
                currentInput = currentSkillInput,
                onCurrentInputChange = { currentSkillInput = it },
                onAddItem = { addSkill(it) },
                onRemoveItem = { removeSkill(it) },
                placeholder = "e.g., Welding, Driving, Plumbing",
                suggestions = listOf("Welding", "Driving", "Plumbing", "Electrical"),
                showDuplicateError = showDuplicateError
            )

            // Actively Seeking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Actively Seeking",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Let employers know you're available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = data.isActivelySeeking,
                    onCheckedChange = {
                        onDataChange(data.copy(isActivelySeeking = it))
                    }
                )
            }
        }
    }
}

@Composable
fun SkillInputSection(
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

        if (itemsList.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(itemsList) { item ->
                    AssistChip(
                        onClick = {},
                        label = { Text(item) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveItem(item) }
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = currentInput,
            onValueChange = onCurrentInputChange,
            label = {
                Text(
                    if (itemsList.isEmpty()) "Add $title" else "Add another $title"
                )
            },
            placeholder = { Text(placeholder) },
            trailingIcon = {
                IconButton(
                    onClick = { onAddItem(currentInput) },
                    enabled = currentInput.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onAddItem(currentInput) }
            ),
            isError = showDuplicateError,
            supportingText = {
                if (showDuplicateError) {
                    Text("Already added")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (suggestions.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = { if (!itemsList.contains(suggestion)) onAddItem(suggestion) },
                        label = { Text(suggestion) },
                        enabled = !itemsList.contains(suggestion)
                    )
                }
            }
        }
    }
}